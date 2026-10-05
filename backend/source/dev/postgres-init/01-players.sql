CREATE TABLE IF NOT EXISTS players (
    player_id VARCHAR(128) PRIMARY KEY
);

-- Identificadores fictícios somente para desenvolvimento local.
INSERT INTO players (player_id) VALUES
    ('player-123'), ('player-456'), ('player-789')
ON CONFLICT (player_id) DO NOTHING;