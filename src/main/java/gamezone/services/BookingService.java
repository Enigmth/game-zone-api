package gamezone.services;

import gamezone.DataBase;
import gamezone.domain.PageResponse;
import gamezone.domain.dto.bookings.BookingResponse;
import gamezone.domain.dto.bookings.CreateBookingRequest;
import gamezone.domain.dto.bookings.CreatePaymentRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BookingService extends AbstractService {
    public BookingResponse createBooking(String userId, String gameId, CreateBookingRequest request) {
        String bookingId = UUID.randomUUID().toString();

        try (Connection connection = DataBase.getConnection()) {
            connection.setAutoCommit(false);
            try {
                LockedGame game = lockGame(connection, gameId);
                int reserved = lockAndGetReservedSpots(connection, gameId);
                int newTotal = reserved + request.playersToAdd();
                if (newTotal > game.maxPlayers()) {
                    throw new BadRequestException("Game is full for requested spots");
                }

                String paymentStatus = game.pricePerPlayer() == null ? "NONE" : "PENDING";
                String insertSql = """
                        INSERT INTO bookings (id, game_id, user_id, spots_reserved, payment_status)
                        VALUES (?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    statement.setString(1, bookingId);
                    statement.setString(2, gameId);
                    statement.setString(3, userId);
                    statement.setInt(4, request.playersToAdd());
                    statement.setString(5, paymentStatus);
                    statement.executeUpdate();
                }

                if (newTotal >= game.maxPlayers()) {
                    updateGameStatus(connection, gameId, "FULL");
                }

                connection.commit();
                return new BookingResponse(bookingId, gameId, paymentStatus.toLowerCase(Locale.ROOT), request.playersToAdd());
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (BadRequestException | NotFoundException exception) {
            throw exception;
        } catch (SQLException exception) {
            throw new WebApplicationException("Failed to create booking", exception, 500);
        }
    }

    public void createPayment(String bookingId, CreatePaymentRequest request) {
        // Payment state should be updated by webhook, not by client-submitted tokens.
        throw new BadRequestException("Direct payment confirmation is not supported. Use provider webhook.");
    }

    public PageResponse<BookingResponse> getMyBookings(String userId, String status, int page, int size) {
        String countSql = "SELECT COUNT(*) FROM bookings WHERE user_id = ?" + (status == null ? "" : " AND payment_status = ?");
        String listSql = """
                SELECT id, game_id, payment_status, spots_reserved
                FROM bookings
                WHERE user_id = ?
                """ + (status == null ? "" : " AND payment_status = ?") + " ORDER BY created_at DESC LIMIT ? OFFSET ?";

        long totalItems = 0;
        List<BookingResponse> items = new ArrayList<>();

        try (Connection connection = DataBase.getConnection()) {
            try (PreparedStatement countStatement = connection.prepareStatement(countSql)) {
                countStatement.setString(1, userId);
                if (status != null) {
                    countStatement.setString(2, status.toUpperCase(Locale.ROOT));
                }
                try (ResultSet resultSet = countStatement.executeQuery()) {
                    if (resultSet.next()) {
                        totalItems = resultSet.getLong(1);
                    }
                }
            }

            try (PreparedStatement listStatement = connection.prepareStatement(listSql)) {
                int index = 1;
                listStatement.setString(index++, userId);
                if (status != null) {
                    listStatement.setString(index++, status.toUpperCase(Locale.ROOT));
                }
                listStatement.setInt(index++, size);
                listStatement.setInt(index, page * size);

                try (ResultSet resultSet = listStatement.executeQuery()) {
                    while (resultSet.next()) {
                        items.add(new BookingResponse(
                                resultSet.getString("id"),
                                resultSet.getString("game_id"),
                                resultSet.getString("payment_status").toLowerCase(Locale.ROOT),
                                resultSet.getInt("spots_reserved")
                        ));
                    }
                }
            }
        } catch (SQLException exception) {
            throw new WebApplicationException("Failed to fetch bookings", exception, 500);
        }

        int totalPages = (int) Math.ceil((double) totalItems / size);
        return new PageResponse<>(items, page, size, totalItems, totalPages);
    }

    public void cancelBooking(String userId, String bookingId) {
        try (Connection connection = DataBase.getConnection()) {
            connection.setAutoCommit(false);
            try {
                LockedBooking booking = lockBooking(connection, bookingId);
                if (!booking.userId().equals(userId)) {
                    throw new ForbiddenException("Booking does not belong to authenticated user");
                }
                if (!"NONE".equals(booking.paymentStatus())) {
                    throw new BadRequestException("Only free bookings can be cancelled directly");
                }

                Instant cancelDeadline = booking.startTime().toInstant().minus(24, ChronoUnit.HOURS);
                if (!Instant.now().isBefore(cancelDeadline)) {
                    throw new BadRequestException("Free game cancellation requires at least 24 hours notice");
                }

                String deleteBookingSql = "DELETE FROM bookings WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteBookingSql)) {
                    statement.setString(1, bookingId);
                    statement.executeUpdate();
                }

                int reserved = lockAndGetReservedSpots(connection, booking.gameId());
                if (reserved < booking.maxPlayers()) {
                    updateGameStatus(connection, booking.gameId(), "OPEN");
                }

                connection.commit();
            } catch (Exception exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (BadRequestException | NotFoundException | ForbiddenException exception) {
            throw exception;
        } catch (SQLException exception) {
            throw new WebApplicationException("Failed to cancel booking", exception, 500);
        }
    }

    private LockedGame lockGame(Connection connection, String gameId) throws SQLException {
        String sql = """
                SELECT max_players, status, price_per_player
                FROM games
                WHERE id = ?
                FOR UPDATE
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new NotFoundException("Game not found");
                }

                String status = resultSet.getString("status");
                if (!"OPEN".equals(status)) {
                    throw new BadRequestException("Game is not open for booking");
                }

                return new LockedGame(
                        resultSet.getInt("max_players"),
                        resultSet.getBigDecimal("price_per_player")
                );
            }
        }
    }

    private LockedBooking lockBooking(Connection connection, String bookingId) throws SQLException {
        String sql = """
                SELECT b.user_id, b.game_id, b.payment_status, g.start_time, g.max_players
                FROM bookings b
                JOIN games g ON g.id = b.game_id
                WHERE b.id = ?
                FOR UPDATE
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, bookingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new NotFoundException("Booking not found");
                }

                return new LockedBooking(
                        resultSet.getString("user_id"),
                        resultSet.getString("game_id"),
                        resultSet.getString("payment_status"),
                        resultSet.getTimestamp("start_time"),
                        resultSet.getInt("max_players")
                );
            }
        }
    }

    private int lockAndGetReservedSpots(Connection connection, String gameId) throws SQLException {
        String sql = """
                SELECT spots_reserved
                FROM bookings
                WHERE game_id = ? AND payment_status IN ('NONE', 'PENDING', 'PAID')
                FOR UPDATE
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                int total = 0;
                while (resultSet.next()) {
                    total += resultSet.getInt("spots_reserved");
                }
                return total;
            }
        }
    }

    private void updateGameStatus(Connection connection, String gameId, String status) throws SQLException {
        String sql = "UPDATE games SET status = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setString(2, gameId);
            statement.executeUpdate();
        }
    }

    private record LockedGame(int maxPlayers, BigDecimal pricePerPlayer) {
    }

    private record LockedBooking(
            String userId,
            String gameId,
            String paymentStatus,
            Timestamp startTime,
            int maxPlayers
    ) {
    }
}
