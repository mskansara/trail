package io.trail.events;

import java.time.Instant;
import java.util.UUID;

public record IncidentEvent(
    UUID eventId,          
        IncidentEventType type,
        UUID incidentId,       
        String tenantId,
        Instant occurredAt,

        // CREATED
        String title,
        String source,

        // STATE_CHANGED
        String fromState,
        String toState,

        // TRIAGED
        String severity
) {
    public static IncidentEvent created(UUID incidentId, String tenantId,
                                        String title, String source) {
        return new IncidentEvent(UUID.randomUUID(), IncidentEventType.CREATED,
                incidentId, tenantId, Instant.now(),
                title, source, null, "OPEN", null);
    }

    public static IncidentEvent stateChanged(UUID incidentId, String tenantId,
                                             String fromState, String toState) {
        return new IncidentEvent(UUID.randomUUID(), IncidentEventType.STATE_CHANGED,
                incidentId, tenantId, Instant.now(),
                null, null, fromState, toState, null);
    }

    public static IncidentEvent triaged(UUID incidentId, String tenantId, String severity) {
        return new IncidentEvent(UUID.randomUUID(), IncidentEventType.TRIAGED,
                incidentId, tenantId, Instant.now(),
                null, null, null, null, severity);
    }
}
