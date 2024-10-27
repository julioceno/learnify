CREATE TABLE permissions (
    id TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    plan_id TEXT NOT NULL,

    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) DEFAULT NULL,

    UNIQUE (name, plan_id),
    FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE,
    CONSTRAINT permissions_pkey PRIMARY KEY (id)
);