CREATE TABLE incidents (
    id           UUID         PRIMARY KEY,
    tenant_id    VARCHAR(128) NOT NULL,
    title        VARCHAR(255) NOT NULL,
    description  VARCHAR(4000),
    source       VARCHAR(64)  NOT NULL,
    severity     VARCHAR(8),                     
    state        VARCHAR(32)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL,
    updated_at   TIMESTAMPTZ  NOT NULL,
    resolved_at  TIMESTAMPTZ,
    version      BIGINT       NOT NULL DEFAULT 0, 

    CONSTRAINT chk_incident_state
        CHECK (state IN ('OPEN','ACKNOWLEDGED','INVESTIGATING','RESOLVED','CLOSED')),
    CONSTRAINT chk_incident_severity
        CHECK (severity IS NULL OR severity IN ('P1','P2','P3','P4'))
);

CREATE INDEX idx_incident_tenant       ON incidents (tenant_id);
CREATE INDEX idx_incident_tenant_state ON incidents (tenant_id, state);


CREATE TABLE state_transitions (
    id           UUID         PRIMARY KEY,
    tenant_id    VARCHAR(128) NOT NULL,
    incident_id  UUID         NOT NULL,
    from_state   VARCHAR(32),                     
    to_state     VARCHAR(32)  NOT NULL,
    event_type   VARCHAR(32),                    
    actor        VARCHAR(128) NOT NULL,
    reason       VARCHAR(2000),
    timestamp    TIMESTAMPTZ  NOT NULL,

    CONSTRAINT fk_transition_incident
        FOREIGN KEY (incident_id) REFERENCES incidents (id),
    CONSTRAINT chk_transition_to_state
        CHECK (to_state IN ('OPEN','ACKNOWLEDGED','INVESTIGATING','RESOLVED','CLOSED'))
);

CREATE INDEX idx_transition_incident ON state_transitions (incident_id, timestamp);
CREATE INDEX idx_transition_tenant   ON state_transitions (tenant_id);