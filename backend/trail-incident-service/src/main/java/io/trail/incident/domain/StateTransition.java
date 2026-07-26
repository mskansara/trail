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
import jakarta.persistence.Table;

@Entity
@Table(
    name = "state_transitions",
    indexes = {
        @Index(name = "idx_state_transition", columnList = "incident_id, timestamp"),
        @Index(name = "idx_transition_tenant", columnList = "tenant_id")
    }
)
public class StateTransition {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    @Column(name = "incident_id", nullable = false, updatable = false)
    private UUID incidentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_state")
    private IncidentState fromState;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_state")
    private IncidentState toState;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private IncidentEvent eventType;

    @Column(nullable = false, updatable = false)
    private String actor;

    @Column(length = 4000)
    private String reason;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;


    protected StateTransition() {
        // for JPA
    }

    private StateTransition(String tenantId, UUID incidentId, IncidentState fromState, IncidentState toState,
                            IncidentEvent eventType, String actor, String reason) {
        this.id = UUID.randomUUID();
        this.tenantId = tenantId;
        this.incidentId = incidentId;
        this.fromState = fromState;
        this.toState = toState;
        this.eventType = eventType;
        this.actor = actor;
        this.reason = reason;
    }

    public static StateTransition creation(Incident incident, String actor) {
        return new StateTransition(
            incident.getTenantId(),
            incident.getId(),
            null,
            IncidentState.OPEN,
            null,
            actor,
            "Incident created"

        );
    }

    @PrePersist
    void onCreate() {
        this.timestamp = Instant.now();
    }

    public UUID getId() { return id; }
    public String getTenantId() { return tenantId; }
    public UUID getIncidentId() { return incidentId; }
    public IncidentState getFromState() { return fromState; }
    public IncidentState getToState() { return toState; }
    public IncidentEvent getEventType() { return eventType; }
    public String getActor() { return actor; }
    public String getReason() { return reason; }
    public Instant getTimestamp() { return timestamp; }
}
