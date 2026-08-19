CREATE TABLE offline_sync_items (
    id UUID PRIMARY KEY,
    store_id UUID NOT NULL REFERENCES stores(id),
    user_id UUID NOT NULL REFERENCES users(id),
    client_mutation_id VARCHAR(255) NOT NULL UNIQUE,
    method VARCHAR(16) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    payload TEXT,
    status VARCHAR(64) NOT NULL,
    error TEXT,
    created_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP
);
