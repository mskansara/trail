package io.trail.incident.domain;

import java.util.Map;
import java.util.Optional;

import static io.trail.incident.domain.IncidentEvent.*;
import static io.trail.incident.domain.IncidentState.*;

public final class IncidentLifecycle {
    private static final Map<IncidentState, Map<IncidentEvent, IncidentState>> TRANSITIONS = Map.of(
            OPEN,          Map.of(ACKNOWLEDGE, ACKNOWLEDGED),
            ACKNOWLEDGED,  Map.of(INVESTIGATE, INVESTIGATING),
            INVESTIGATING, Map.of(RESOLVE, RESOLVED),
            RESOLVED,      Map.of(CLOSE, CLOSED, REOPEN, INVESTIGATING),
            CLOSED,        Map.of() 
    );

    private IncidentLifecycle() {
        // prevent instantiation
    }

    public static Optional<IncidentState> next(IncidentState current, IncidentEvent event) {
        return Optional.ofNullable(TRANSITIONS.get(current))
        .map(byEvent -> byEvent.get(event));
    }
}
