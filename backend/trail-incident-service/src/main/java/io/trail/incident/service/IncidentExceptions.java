package io.trail.incident.service;

import java.util.UUID;

import io.trail.incident.domain.IncidentEvent;
import io.trail.incident.domain.IncidentState;

public final class IncidentExceptions {

    private IncidentExceptions(){

    }

    public static class NotFound extends RuntimeException {
        public NotFound(UUID id) {
            super("Incident not found: " + id);
        }
    }

    public static class IllegalTransition extends RuntimeException {
        public IllegalTransition(IncidentState from, IncidentEvent event) {
            super("Event " + event + " is not permitted from state " + from);
        }
    }
}
