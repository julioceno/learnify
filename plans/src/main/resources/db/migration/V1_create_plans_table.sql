CREATE TABLE plans (
    id TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    value FLOAT NOT NULL,
    expiresIn TIMESTAMP(3) NOT NULL,

    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) DEFAULT NULL,

    CONSTRAINT plans_pkey PRIMARY KEY (id)
);