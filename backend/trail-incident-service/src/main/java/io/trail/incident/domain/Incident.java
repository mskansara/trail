package io.trail.incident.domain;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(
name = "incidents",
indexes = {
        @Index(name = "idx_incident_tenant", columnList = "tenant_id"),
        @Index(name = "idx_incident_tenant_state", columnList = "tenant_id, state"),
    }
)
public class Incident {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String description;

    @Column(nullable = false)
    private String source;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentState state;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Version
    private long version;

    protected Incident() {
        // for JPA
    }
   

    private Incident(String tenantId, String title, String description, String source) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.title = title;
        this.description = description;
        this.source = source;
        this.severity = null;
        this.state = IncidentState.OPEN;
    }

    public static Incident open(String tenantId, String title, String description, String source) {
        return new Incident(tenantId, title, description, source);
    }

    public void moveTo(IncidentState newState) {
        this.state = newState;
        if (newState == IncidentState.RESOLVED) {
            this.resolvedAt = Instant.now();
        }
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSource() { return source; }
    public Severity getSeverity() { return severity; }
    public IncidentState getState() { return state; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public long getVersion() { return version; }

}
