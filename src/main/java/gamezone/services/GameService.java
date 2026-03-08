package gamezone.services;

import gamezone.DataBase;
import gamezone.domain.PageResponse;
import gamezone.domain.dto.games.CreateGameRequest;
import gamezone.domain.dto.games.CreateGameResponse;
import gamezone.domain.dto.games.GameDetailResponse;
import gamezone.domain.dto.games.GameListItemResponse;
import gamezone.domain.dto.games.ParticipantResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameService extends AbstractService {

    public PageResponse<GameListItemResponse> getGames(int page, int size) {
        long totalItems = 0;
        List<GameListItemResponse> items = new ArrayList<>();

        String countSql = "SELECT COUNT(*) FROM games";
        String listSql = """
                SELECT g.id,
                       g.title,
                       g.status,
                       CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, '')) AS organizer_name,
                       f.name AS facility_name,
                       g.max_players,
                       g.start_time,
                       g.price_per_player,
                       COALESCE(SUM(CASE WHEN b.payment_status IN ('NONE', 'PENDING', 'PAID') THEN b.spots_reserved ELSE 0 END), 0) AS booked_spots
                FROM games g
                JOIN users u ON u.id = g.host_id
                JOIN facilities f ON f.id = g.facility_id
                LEFT JOIN bookings b ON b.game_id = g.id
                GROUP BY g.id, g.title, g.status, u.first_name, u.last_name, f.name, g.max_players, g.start_time, g.price_per_player
                ORDER BY g.start_time ASC
                LIMIT ? OFFSET ?
                """;

        try (Connection connection = DataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(countSql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    totalItems = resultSet.getLong(1);
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(listSql)) {
                statement.setInt(1, size);
                statement.setInt(2, page * size);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int maxPlayers = resultSet.getInt("max_players");
                        int bookedSpots = resultSet.getInt("booked_spots");
                        int spotsLeft = Math.max(0, maxPlayers - bookedSpots);
                        BigDecimal price = resultSet.getBigDecimal("price_per_player");

                        items.add(new GameListItemResponse(
                                resultSet.getString("id"),
                                resultSet.getString("title"),
                                resultSet.getString("status").toLowerCase(),
                                resultSet.getString("organizer_name").trim(),
                                resultSet.getString("facility_name"),
                                "all",
                                spotsLeft,
                                resultSet.getTimestamp("start_time").toInstant().toString(),
                                price == null ? "Free" : price.stripTrailingZeros().toPlainString(),
                                null
                        ));
                    }
                }
            }
        } catch (SQLException exception) {
            throw new WebApplicationException("Failed to load games", exception, 500);
        }

        int totalPages = (int) Math.ceil((double) totalItems / size);
        return new PageResponse<>(items, page, size, totalItems, totalPages);
    }

    public GameDetailResponse getGameById(String gameId) {
        String sql = """
                SELECT g.id,
                       g.title,
                       g.status,
                       CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, '')) AS organizer_name,
                       f.name AS facility_name,
                       g.max_players,
                       g.start_time,
                       g.price_per_player,
                       COALESCE(SUM(CASE WHEN b.payment_status IN ('NONE', 'PENDING', 'PAID') THEN b.spots_reserved ELSE 0 END), 0) AS booked_spots
                FROM games g
                JOIN users u ON u.id = g.host_id
                JOIN facilities f ON f.id = g.facility_id
                LEFT JOIN bookings b ON b.game_id = g.id
                WHERE g.id = ?
                GROUP BY g.id, g.title, g.status, u.first_name, u.last_name, f.name, g.max_players, g.start_time, g.price_per_player
                """;

        try (Connection connection = DataBase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gameId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new NotFoundException("Game not found");
                }

                int maxPlayers = resultSet.getInt("max_players");
                int bookedSpots = resultSet.getInt("booked_spots");
                int spotsLeft = Math.max(0, maxPlayers - bookedSpots);
                BigDecimal price = resultSet.getBigDecimal("price_per_player");

                return new GameDetailResponse(
                        resultSet.getString("id"),
                        resultSet.getString("title"),
                        resultSet.getString("status").toLowerCase(),
                        resultSet.getString("organizer_name").trim(),
                        resultSet.getString("facility_name"),
                        "all",
                        maxPlayers,
                        spotsLeft,
                        resultSet.getTimestamp("start_time").toInstant().toString(),
                        price == null ? null : price.multiply(BigDecimal.valueOf(100)).intValue(),
                        true,
                        List.of()
                );
            }
        } catch (SQLException exception) {
            throw new WebApplicationException("Failed to load game", exception, 500);
        }
    }

    public CreateGameResponse createGame(String hostId, CreateGameRequest request) {
        String gameId = UUID.randomUUID().toString();
        String facilityId = UUID.randomUUID().toString();
        String insertFacilitySql = """
                INSERT INTO facilities (id, name, location_name)
                VALUES (?, ?, ?)
                """;
        String insertGameSql = """
                INSERT INTO games (
                    id, host_id, facility_id, title, max_players, price_per_player,
                    status, start_time, duration_minutes
                ) VALUES (?, ?, ?, ?, ?, ?, 'OPEN', ?, ?)
                """;

        try (Connection connection = DataBase.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement facilityStatement = connection.prepareStatement(insertFacilitySql);
                 PreparedStatement gameStatement = connection.prepareStatement(insertGameSql)) {
                facilityStatement.setString(1, facilityId);
                facilityStatement.setString(2, request.location);
                facilityStatement.setString(3, request.location);
                facilityStatement.executeUpdate();

                gameStatement.setString(1, gameId);
                gameStatement.setString(2, hostId);
                gameStatement.setString(3, facilityId);
                gameStatement.setString(4, request.title);
                gameStatement.setInt(5, request.players);
                if (Boolean.TRUE.equals(request.isFree)) {
                    gameStatement.setNull(6, java.sql.Types.DECIMAL);
                } else {
                    gameStatement.setBigDecimal(6, BigDecimal.valueOf(request.priceCents).movePointLeft(2));
                }
                gameStatement.setTimestamp(7, Timestamp.valueOf(request.date.atTime(request.time)));
                gameStatement.setInt(8, request.durationMinutes);
                gameStatement.executeUpdate();

                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new WebApplicationException("Failed to create game", exception, 500);
        }

        GameDetailResponse game = new GameDetailResponse(
                gameId,
                request.title,
                "open",
                "me",
                request.location,
                "all",
                request.players,
                request.players,
                request.date.atTime(request.time).toInstant(ZoneOffset.UTC).toString(),
                request.priceCents,
                true,
                List.of()
        );

        return new CreateGameResponse(game, "https://gamezone.app/games/" + gameId);
    }

    public List<ParticipantResponse> getParticipants(String gameId) {
        String sql = """
                SELECT DISTINCT u.id,
                       CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, '')) AS full_name
                FROM bookings b
                JOIN users u ON u.id = b.user_id
                WHERE b.game_id = ? AND b.payment_status IN ('NONE', 'PENDING', 'PAID')
                ORDER BY full_name ASC
                """;

        List<ParticipantResponse> participants = new ArrayList<>();

        try (Connection connection = DataBase.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, gameId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    participants.add(new ParticipantResponse(
                            resultSet.getString("id"),
                            resultSet.getString("full_name").trim(),
                            null
                    ));
                }
            }
        } catch (SQLException exception) {
            throw new WebApplicationException("Failed to load participants", exception, 500);
        }

        return participants;
    }
}
