package io.trail.incident.web.dto;

import java.lang.Thread.State;
import java.util.UUID;

import io.trail.incident.domain.IncidentState;
import io.trail.incident.domain.StateTransition;

public record TransitionResponse(
    UUID id,
    IncidentState fromState,
    IncidentState toState,
    String eventType,
    String actor,
    String reason,
    String timestamp
) {
    public static TransitionResponse from(StateTransition t) {
        return new TransitionResponse(
            t.getId(),
            t.getFromState(),
            t.getToState(),
            t.getEventType() != null ? t.getEventType().name() : null,
            t.getActor(),
            t.getReason(),
            t.getTimestamp().toString()
        );
    }
}
