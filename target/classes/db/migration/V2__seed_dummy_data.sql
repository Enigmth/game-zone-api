INSERT IGNORE INTO users (
    id, email, password_hash, first_name, last_name, rating, win_streak, total_minutes_played, preferred_position, games_played, facilities_played_count
) VALUES
    ('11111111-1111-1111-1111-111111111111', 'alex@gamezone.app', 'dummy-hash-1', 'Alex', 'Carter', 1020, 2, 180, 'Midfielder', 12, 3),
    ('22222222-2222-2222-2222-222222222222', 'maria@gamezone.app', 'dummy-hash-2', 'Maria', 'Silva', 995, 1, 120, 'Defender', 8, 2),
    ('33333333-3333-3333-3333-333333333333', 'james@gamezone.app', 'dummy-hash-3', 'James', 'Lee', 1010, 0, 90, 'Forward', 6, 2);

INSERT IGNORE INTO friendships (
    id, requester_id, addressee_id, status
) VALUES
    ('44444444-4444-4444-4444-444444444441', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'ACCEPTED'),
    ('44444444-4444-4444-4444-444444444442', '22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333333', 'PENDING');

INSERT IGNORE INTO facilities (
    id, name, location_name, address, latitude, longitude
) VALUES
    ('55555555-5555-5555-5555-555555555551', 'Downtown Arena', 'City Center', '101 Main St', 41.99810, 21.42540),
    ('55555555-5555-5555-5555-555555555552', 'Riverside Court', 'Riverside', '25 River Rd', 42.00120, 21.43410);

INSERT IGNORE INTO games (
    id, host_id, facility_id, title, start_time, duration_minutes, max_players, price_per_player, status
) VALUES
    ('66666666-6666-6666-6666-666666666661', '11111111-1111-1111-1111-111111111111', '55555555-5555-5555-5555-555555555551', 'Saturday Football 5v5', '2026-03-14 18:00:00', 90, 10, NULL, 'OPEN'),
    ('66666666-6666-6666-6666-666666666662', '22222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555552', 'Evening Padel Doubles', '2026-03-16 20:00:00', 60, 4, 6.50, 'OPEN'),
    ('66666666-6666-6666-6666-666666666663', '33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555551', 'Sunday Match Review', '2026-02-20 19:00:00', 90, 10, NULL, 'COMPLETED');

INSERT IGNORE INTO bookings (
    id, game_id, user_id, spots_reserved, payment_status
) VALUES
    ('77777777-7777-7777-7777-777777777771', '66666666-6666-6666-6666-666666666661', '22222222-2222-2222-2222-222222222222', 2, 'NONE'),
    ('77777777-7777-7777-7777-777777777772', '66666666-6666-6666-6666-666666666662', '11111111-1111-1111-1111-111111111111', 1, 'PENDING'),
    ('77777777-7777-7777-7777-777777777773', '66666666-6666-6666-6666-666666666663', '22222222-2222-2222-2222-222222222222', 1, 'PAID');

INSERT IGNORE INTO player_entries (
    id, booking_id, player_user_id, guest_name
) VALUES
    ('88888888-8888-8888-8888-888888888881', '77777777-7777-7777-7777-777777777771', '22222222-2222-2222-2222-222222222222', NULL),
    ('88888888-8888-8888-8888-888888888882', '77777777-7777-7777-7777-777777777771', NULL, 'Guest Player');

INSERT IGNORE INTO payments (
    id, booking_id, provider, reference_id, amount, status
) VALUES
    ('99999999-9999-9999-9999-999999999991', '77777777-7777-7777-7777-777777777772', 'STRIPE', 'pi_dummy_001', 6.50, 'CREATED');

INSERT IGNORE INTO game_results (
    id, game_id, winning_team, submitted_by, confirmed
) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', '66666666-6666-6666-6666-666666666663', 1, '33333333-3333-3333-3333-333333333333', TRUE);

INSERT IGNORE INTO game_player_stats (
    id, game_id, user_id, team, minutes_played, won
) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', '66666666-6666-6666-6666-666666666663', '22222222-2222-2222-2222-222222222222', 1, 90, TRUE),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', '66666666-6666-6666-6666-666666666663', '33333333-3333-3333-3333-333333333333', 2, 90, FALSE);

INSERT IGNORE INTO conversations (
    id, type
) VALUES
    ('cccccccc-cccc-cccc-cccc-ccccccccccc1', 'DIRECT'),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'GROUP');

INSERT IGNORE INTO conversation_members (
    conversation_id, user_id
) VALUES
    ('cccccccc-cccc-cccc-cccc-ccccccccccc1', '11111111-1111-1111-1111-111111111111'),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc1', '22222222-2222-2222-2222-222222222222'),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc2', '11111111-1111-1111-1111-111111111111'),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc2', '22222222-2222-2222-2222-222222222222'),
    ('cccccccc-cccc-cccc-cccc-ccccccccccc2', '33333333-3333-3333-3333-333333333333');

INSERT IGNORE INTO messages (
    id, conversation_id, sender_id, content
) VALUES
    ('dddddddd-dddd-dddd-dddd-ddddddddddd1', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', '11111111-1111-1111-1111-111111111111', 'Hey, are you joining tonight?'),
    ('dddddddd-dddd-dddd-dddd-ddddddddddd2', 'cccccccc-cccc-cccc-cccc-ccccccccccc1', '22222222-2222-2222-2222-222222222222', 'Yes, I will be there in 20 minutes.'),
    ('dddddddd-dddd-dddd-dddd-ddddddddddd3', 'cccccccc-cccc-cccc-cccc-ccccccccccc2', '33333333-3333-3333-3333-333333333333', 'Great match everyone.');
