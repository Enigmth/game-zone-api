# Gamezone API

Jersey-based Java 25 backend scaffold for the Loja mobile app contract.

## Tech
- Java 25
- Jersey 3 (`jakarta.ws.rs`)
- MySQL (Aurora-compatible)
- Flyway
- Jackson
- Bean Validation
- JWT filter scaffold
- OpenAPI dependencies included

## Base Rules
- Base path: `/api/v1`
- JSON only
- UTC timestamps (ISO-8601)
- Pagination format:
```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalItems": 0,
  "totalPages": 0
}
```
- Standard error format via global exception mapper.

## Implemented Endpoint Surface (stubs)
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `GET /games`
- `GET /games/{gameId}`
- `POST /games`
- `GET /games/{gameId}/participants`
- `POST /games/{gameId}/bookings`
- `POST /bookings/{bookingId}/payments`
- `GET /users/me/bookings`
- `DELETE /bookings/{bookingId}`
- `POST /games/{gameId}/ratings`
- `GET /users/me/friends`
- `GET /users/me/conversations`
- `GET /conversations/{conversationId}/messages`
- `POST /conversations/{conversationId}/messages`
- `GET /users/me`
- `PATCH /users/me`
- `GET /health/live`
- `GET /health/ready`

## Notes
- Phase 1 endpoints now run against JDBC + MySQL.
- `FlywayMigrator` runs on app startup and applies `V1__init_core_schema.sql`.
- `JwtAuthFilter` currently checks Bearer token presence and requires `X-User-Id` header; token verification TODO.
- Payment token storage is intentionally omitted (must be handled by provider references only).
- Booking creation uses `SELECT ... FOR UPDATE` to enforce `spots_reserved <= max_players`.
- Free booking cancellation is enforced to `>= 24h` before game start.
- Database config is read from OS env vars first, then from local `.env` (if present).
