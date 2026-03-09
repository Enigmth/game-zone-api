ALTER TABLE users
    MODIFY email VARCHAR(255) NULL,
    MODIFY password_hash VARCHAR(255) NULL,
    ADD COLUMN phone VARCHAR(20) NULL UNIQUE AFTER email;

CREATE TABLE IF NOT EXISTS phone_otps (
    id         CHAR(36)    PRIMARY KEY,
    phone      VARCHAR(20) NOT NULL,
    code       CHAR(6)     NOT NULL,
    expires_at DATETIME    NOT NULL,
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_po_phone (phone)
);
