package gamezone.services;

import gamezone.DataBase;
import gamezone.domain.PageResponse;
import gamezone.domain.dto.users.FriendListItemResponse;
import gamezone.domain.dto.users.PatchUserProfileRequest;
import gamezone.domain.dto.users.UserProfileResponse;
import jakarta.ws.rs.NotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class UserService extends AbstractService {

    public UserProfileResponse getMe(String userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement(
                    "SELECT id, first_name, last_name, preferred_position, " +
                    "games_played, total_minutes_played, facilities_played_count, win_streak " +
                    "FROM users WHERE id = ?");
            ps.setString(1, userId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new NotFoundException("User not found");
            }
            String fullName = buildFullName(rs.getString("first_name"), rs.getString("last_name"));
            int totalMinutes = rs.getInt("total_minutes_played");
            return new UserProfileResponse(
                    rs.getString("id"),
                    fullName,
                    null,
                    0.0,
                    0,
                    rs.getString("preferred_position"),
                    rs.getInt("games_played"),
                    totalMinutes / 60,
                    rs.getInt("facilities_played_count"),
                    rs.getInt("win_streak")
            );
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user profile", e);
        } finally {
            close(rs, null, null);
            closePreparedStatements(ps);
            closeConnections(conn);
        }
    }

    public UserProfileResponse patchMe(String userId, PatchUserProfileRequest request) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DataBase.getConnection();
            ps = conn.prepareStatement("UPDATE users SET preferred_position = ? WHERE id = ?");
            ps.setString(1, request.preferredPosition());
            ps.setString(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user profile", e);
        } finally {
            closePreparedStatements(ps);
            closeConnections(conn);
        }
        return getMe(userId);
    }

    public PageResponse<FriendListItemResponse> getMyFriends(String userId, int page, int size) {
        Connection conn = null;
        PreparedStatement countPs = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DataBase.getConnection();

            // Count accepted friends
            countPs = conn.prepareStatement(
                    "SELECT COUNT(*) FROM friendships " +
                    "WHERE (requester_id = ? OR addressee_id = ?) AND status = 'ACCEPTED'");
            countPs.setString(1, userId);
            countPs.setString(2, userId);
            ResultSet countRs = countPs.executeQuery();
            int total = countRs.next() ? countRs.getInt(1) : 0;
            countRs.close();

            // Fetch friend user rows
            ps = conn.prepareStatement(
                    "SELECT u.id, u.first_name, u.last_name, u.win_streak, u.games_played " +
                    "FROM friendships f " +
                    "JOIN users u ON u.id = CASE WHEN f.requester_id = ? THEN f.addressee_id ELSE f.requester_id END " +
                    "WHERE (f.requester_id = ? OR f.addressee_id = ?) AND f.status = 'ACCEPTED' " +
                    "LIMIT ? OFFSET ?");
            ps.setString(1, userId);
            ps.setString(2, userId);
            ps.setString(3, userId);
            ps.setInt(4, size);
            ps.setInt(5, page * size);
            rs = ps.executeQuery();

            List<FriendListItemResponse> items = new java.util.ArrayList<>();
            while (rs.next()) {
                String friendName = buildFullName(rs.getString("first_name"), rs.getString("last_name"));
                items.add(new FriendListItemResponse(
                        rs.getString("id"),
                        friendName,
                        null,
                        0,
                        0,
                        0.0,
                        0,
                        0,
                        rs.getInt("win_streak")
                ));
            }
            int totalPages = total == 0 ? 1 : (int) Math.ceil((double) total / size);
            return new PageResponse<>(items, page, size, total, totalPages);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch friends", e);
        } finally {
            close(rs, null, null);
            closePreparedStatements(countPs, ps);
            closeConnections(conn);
        }
    }

    private String buildFullName(String firstName, String lastName) {
        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";
        return (firstName + " " + lastName).trim();
    }
}
