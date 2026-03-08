CREATE TABLE IF NOT EXISTS refresh_tokens (
    token CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_rt_user ON refresh_tokens(user_id);
