package io.trail.incident.web.dto;

import io.trail.incident.domain.IncidentEvent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TransitionRequest(
    @NotNull(message = "event is required")
    IncidentEvent event,

    @Size(max = 2000)
    String reason
) {
    
}
