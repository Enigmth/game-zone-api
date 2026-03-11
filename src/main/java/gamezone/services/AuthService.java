package gamezone.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import gamezone.DataBase;
import gamezone.common.JwtUtil;
import gamezone.domain.NamedParameterStatement;
import gamezone.domain.dto.auth.AuthTokensResponse;
import gamezone.domain.dto.auth.LoginRequest;
import gamezone.domain.dto.auth.PhoneRequestRequest;
import gamezone.domain.dto.auth.PhoneVerifyRequest;
import gamezone.domain.dto.auth.RefreshRequest;
import gamezone.domain.dto.auth.RegisterRequest;
import gamezone.domain.dto.users.User;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

public class AuthService extends AbstractService {

    private static final long REFRESH_TOKEN_TTL_DAYS = 30;

    public AuthTokensResponse register(RegisterRequest request) {
        Connection conn = null;
        PreparedStatement checkPs = null;
        PreparedStatement insertPs = null;
        ResultSet rs = null;
        try {
            conn = DataBase.getConnection();

            // Check if email already taken
            checkPs = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
            checkPs.setString(1, request.email().toLowerCase().trim());
            rs = checkPs.executeQuery();
            if (rs.next()) {
                throw new BadRequestException("Email already registered");
            }
            rs.close();
            checkPs.close();

            String userId = UUID.randomUUID().toString();
            String passwordHash = BCrypt.withDefaults().hashToString(12, request.password().toCharArray());

            // Split fullName into first/last
            String fullName = request.fullName().trim();
            int spaceIdx = fullName.indexOf(' ');
            String firstName = spaceIdx > 0 ? fullName.substring(0, spaceIdx) : fullName;
            String lastName = spaceIdx > 0 ? fullName.substring(spaceIdx + 1) : "";

            insertPs = conn.prepareStatement(
                    "INSERT INTO users (id, email, password_hash, first_name, last_name) VALUES (?, ?, ?, ?, ?)");
            insertPs.setString(1, userId);
            insertPs.setString(2, request.email().toLowerCase().trim());
            insertPs.setString(3, passwordHash);
            insertPs.setString(4, firstName);
            insertPs.setString(5, lastName);
            insertPs.executeUpdate();

            return issueTokens(conn, userId);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Registration failed", e);
        } finally {
            close(rs, null, null);
            closePreparedStatements(checkPs, insertPs);
            closeConnections(conn);
        }
    }

    public AuthTokensResponse login(LoginRequest request) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement("SELECT id, password_hash FROM users WHERE email = ?");
            ps.setString(1, request.email().toLowerCase().trim());
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new NotAuthorizedException("Invalid email or password");
            }
            String userId = rs.getString("id");
            String storedHash = rs.getString("password_hash");

            BCrypt.Result result = BCrypt.verifyer().verify(request.password().toCharArray(), storedHash.toCharArray());
            if (!result.verified) {
                throw new NotAuthorizedException("Invalid email or password");
            }

            rs.close();
            ps.close();

            return issueTokens(conn, userId);
        } catch (NotAuthorizedException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Login failed", e);
        } finally {
            close(rs, null, null);
            closePreparedStatements(ps);
            closeConnections(conn);
        }
    }

    public AuthTokensResponse refresh(RefreshRequest request) {
        Connection conn = null;
        PreparedStatement selectPs = null;
        PreparedStatement deletePs = null;
        ResultSet rs = null;
        try {
            conn = DataBase.getConnection();
            selectPs = conn.prepareStatement(
                    "SELECT user_id, expires_at FROM refresh_tokens WHERE token = ?");
            selectPs.setString(1, request.refreshToken());
            rs = selectPs.executeQuery();
            if (!rs.next()) {
                throw new NotAuthorizedException("Invalid refresh token");
            }
            String userId = rs.getString("user_id");
            Timestamp expiresAt = rs.getTimestamp("expires_at");
            rs.close();
            selectPs.close();

            if (expiresAt.toInstant().isBefore(Instant.now())) {
                throw new NotAuthorizedException("Refresh token expired");
            }

            // Rotate: delete old token
            deletePs = conn.prepareStatement("DELETE FROM refresh_tokens WHERE token = ?");
            deletePs.setString(1, request.refreshToken());
            deletePs.executeUpdate();

            return issueTokens(conn, userId);
        } catch (NotAuthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed", e);
        } finally {
            close(rs, null, null);
            closePreparedStatements(selectPs, deletePs);
            closeConnections(conn);
        }
    }

    public void logout(String refreshToken) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement("DELETE FROM refresh_tokens WHERE token = ?");
            ps.setString(1, refreshToken);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Logout failed", e);
        } finally {
            closePreparedStatements(ps);
            closeConnections(conn);
        }
    }

    public void requestPhoneCode(PhoneRequestRequest request) {
        String phone = request.phone().trim();
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        String id = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);

        try (Connection conn = DataBase.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO phone_otps (id, phone, code, expires_at) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, phone);
            ps.setString(3, code);
            ps.setTimestamp(4, Timestamp.from(expiresAt));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to store OTP", e);
        }

        // Demo: print instead of sending via WhatsApp
        System.out.println("[DEMO] WhatsApp OTP for " + phone + " → " + code);
    }

    public AuthTokensResponse verifyPhoneCode(PhoneVerifyRequest request) {
        String phone = request.phone().trim();
        // Demo: skip OTP validation — any 6-digit code is accepted

        try (Connection conn = DataBase.getConnection()) {
            String userId;

            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE phone = ?")) {
                ps.setString(1, phone);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getString("id");
                    } else {
                        // New user — create account from phone only
                        User user = new User();
                        user.setId(UUID.randomUUID().toString());
                        userId = user.getId();
                        user.setPhone(phone);
                        NamedParameterStatement insert = new NamedParameterStatement(conn, "INSERT INTO users (\n" +
                                "    id, email, password_hash, first_name, last_name, rating, win_streak, total_minutes_played, preferred_position, games_played, facilities_played_count, phone\n" +
                                ") VALUES (:id, :email, :password_hash, :first_name, :last_name, :rating, :win_streak, :total_minutes_played, :preferred_position, :games_played, :facilities_played_count, :phone)");
                        insert.setString("id", user.getId());
                        insert.setString("email", user.getEmail());
                        insert.setString("password_hash", user.getPasswordHash());
                        insert.setString("first_name", user.getFirstName());
                        insert.setString("last_name", user.getLastName());
                        insert.setInt("rating", user.getRating());
                        insert.setInt("win_streak", user.getWinStreak());
                        insert.setInt("total_minutes_played", user.getTotalMinutesPlayed());
                        insert.setString("preferred_position", user.getPreferredPosition());
                        insert.setInt("games_played", user.getGamesPlayed());
                        insert.setInt("facilities_played_count", user.getFacilitiesPlayedCount());
                        insert.setString("phone", user.getPhone());
                        insert.executeUpdate();
                    }
                }
            }

            return issueTokens(conn, userId);
        } catch (BadRequestException | NotAuthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Phone verification failed", e);
        }
    }

    private AuthTokensResponse issueTokens(Connection conn, String userId) throws Exception {
        String accessToken = JwtUtil.generateAccessToken(userId);
        String refreshToken = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(REFRESH_TOKEN_TTL_DAYS, ChronoUnit.DAYS);

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO refresh_tokens (token, user_id, expires_at) VALUES (?, ?, ?)");
        ps.setString(1, refreshToken);
        ps.setString(2, userId);
        ps.setTimestamp(3, Timestamp.from(expiresAt));
        ps.executeUpdate();
        ps.close();

        return new AuthTokensResponse(accessToken, refreshToken);
    }
}
