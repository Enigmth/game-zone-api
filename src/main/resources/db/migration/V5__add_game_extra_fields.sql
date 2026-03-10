ALTER TABLE games
    ADD COLUMN sport        VARCHAR(50) NULL AFTER status,
    ADD COLUMN is_public    BOOLEAN     NOT NULL DEFAULT TRUE AFTER sport,
    ADD COLUMN team_a_color VARCHAR(7)  NULL AFTER is_public,
    ADD COLUMN team_b_color VARCHAR(7)  NULL AFTER team_a_color;
