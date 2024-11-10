CREATE TABLE signatures (
    id TEXT NOT NULL,
    user_id TEXT NOT NULL UNIQUE,
    plan_id TEXT NOT NULL,

    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) DEFAULT NULL,

    CONSTRAINT signatures_pkey PRIMARY KEY (id)
);

CREATE TABLE signature_permissions (
    id TEXT NOT NULL,
    name TEXT NOT NULL,

    signature_id TEXT NOT NULl,
    permission_id TEXT NOT NULL,

    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) DEFAULT NULL,

    FOREIGN KEY (signature_id) REFERENCES signatures(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id),
    CONSTRAINT permissions_plan_pkey PRIMARY KEY (id)
);