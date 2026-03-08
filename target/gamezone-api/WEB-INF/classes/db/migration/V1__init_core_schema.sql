CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    rating INT DEFAULT 1000,
    win_streak INT DEFAULT 0,
    total_minutes_played INT DEFAULT 0,
    preferred_position VARCHAR(50),
    games_played INT DEFAULT 0,
    facilities_played_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS friendships (
    id CHAR(36) PRIMARY KEY,
    requester_id CHAR(36) NOT NULL,
    addressee_id CHAR(36) NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'BLOCKED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_friend_requester FOREIGN KEY (requester_id) REFERENCES users(id),
    CONSTRAINT fk_friend_addressee FOREIGN KEY (addressee_id) REFERENCES users(id),
    UNIQUE(requester_id, addressee_id)
);

CREATE TABLE IF NOT EXISTS facilities (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location_name VARCHAR(255),
    address VARCHAR(255),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS games (
    id CHAR(36) PRIMARY KEY,
    host_id CHAR(36) NOT NULL,
    facility_id CHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    start_time DATETIME NOT NULL,
    duration_minutes INT NOT NULL,
    max_players INT NOT NULL,
    price_per_player DECIMAL(10,2) NULL,
    status ENUM('OPEN','FULL','CANCELLED','COMPLETED') DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_games_host FOREIGN KEY (host_id) REFERENCES users(id),
    CONSTRAINT fk_games_facility FOREIGN KEY (facility_id) REFERENCES facilities(id)
);

CREATE INDEX idx_games_start_time ON games(start_time);
CREATE INDEX idx_games_status ON games(status);

CREATE TABLE IF NOT EXISTS bookings (
    id CHAR(36) PRIMARY KEY,
    game_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    spots_reserved INT NOT NULL,
    payment_status ENUM('NONE','PENDING','PAID','FAILED') DEFAULT 'NONE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_game FOREIGN KEY (game_id) REFERENCES games(id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_booking_game ON bookings(game_id);
CREATE INDEX idx_booking_user ON bookings(user_id);

CREATE TABLE IF NOT EXISTS player_entries (
    id CHAR(36) PRIMARY KEY,
    booking_id CHAR(36) NOT NULL,
    player_user_id CHAR(36) NULL,
    guest_name VARCHAR(255) NULL,
    CONSTRAINT fk_player_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_player_user FOREIGN KEY (player_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS payments (
    id CHAR(36) PRIMARY KEY,
    booking_id CHAR(36) NOT NULL,
    provider VARCHAR(100),
    reference_id VARCHAR(255),
    amount DECIMAL(10,2),
    status ENUM('CREATED','CONFIRMED','FAILED') DEFAULT 'CREATED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE TABLE IF NOT EXISTS game_results (
    id CHAR(36) PRIMARY KEY,
    game_id CHAR(36) NOT NULL UNIQUE,
    winning_team INT NOT NULL,
    submitted_by CHAR(36),
    confirmed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_result_game FOREIGN KEY (game_id) REFERENCES games(id),
    CONSTRAINT fk_result_user FOREIGN KEY (submitted_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS game_player_stats (
    id CHAR(36) PRIMARY KEY,
    game_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    team INT NOT NULL,
    minutes_played INT NOT NULL,
    won BOOLEAN NOT NULL,
    CONSTRAINT fk_gps_game FOREIGN KEY (game_id) REFERENCES games(id),
    CONSTRAINT fk_gps_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_gps_user ON game_player_stats(user_id);

CREATE TABLE IF NOT EXISTS conversations (
    id CHAR(36) PRIMARY KEY,
    type ENUM('DIRECT','GROUP') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conversation_members (
    conversation_id CHAR(36) NOT NULL,
    user_id CHAR(36) NOT NULL,
    PRIMARY KEY (conversation_id, user_id),
    CONSTRAINT fk_cm_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    CONSTRAINT fk_cm_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS messages (
    id CHAR(36) PRIMARY KEY,
    conversation_id CHAR(36) NOT NULL,
    sender_id CHAR(36) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES users(id)
);

CREATE INDEX idx_messages_conversation ON messages(conversation_id);
