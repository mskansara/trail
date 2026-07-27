package io.trail.incident.web.dto;

import java.time.Instant;
import java.util.UUID;

import io.trail.incident.domain.Incident;
import io.trail.incident.domain.IncidentState;
import io.trail.incident.domain.Severity;

public record IncidentResponse(
    UUID id,
        String tenantId,
        String title,
        String description,
        String source,
        Severity severity,
        IncidentState state,
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt
    
) {
    public static IncidentResponse from(Incident i) {
        return new IncidentResponse(i.getId(), i.getTenantId(), i.getTitle(), i.getDescription(), i.getSource(), i.getSeverity(), i.getState(), i.getCreatedAt(), i.getUpdatedAt(), i.getResolvedAt());
    }
}
