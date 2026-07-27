package io.trail.incident.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.trail.incident.domain.Incident;
import io.trail.incident.domain.IncidentEvent;
import io.trail.incident.domain.IncidentLifecycle;
import io.trail.incident.domain.IncidentState;
import io.trail.incident.domain.StateTransition;
import io.trail.incident.repository.IncidentRepository;
import io.trail.incident.repository.StateTransitionRepository;


@Service
public class IncidentService {

    private final IncidentRepository incidents;
    private final StateTransitionRepository transitions;

    public IncidentService(IncidentRepository incidents, StateTransitionRepository transitions) {
        this.incidents = incidents;
        this.transitions = transitions;
    }

    @Transactional
    public Incident create(String tenantId, String title, String description, String source, String actor) {
        Incident incident = Incident.open(tenantId, title, description, source);
        incidents.save(incident);
        transitions.save(StateTransition.creation(incident, actor));
        return incident;
    }

    @Transactional
    public Incident transition(String tenantId, UUID incidentId, IncidentEvent event, String actor, String reason) {
        Incident incident = incidents.findByIdAndTenantId(incidentId, tenantId)
        .orElseThrow(() -> new IncidentExceptions.NotFound(incidentId));

        IncidentState from = incident.getState();
        IncidentState to = IncidentLifecycle.next(from, event)
        .orElseThrow(() -> new IncidentExceptions.IllegalTransition(from, event));

        incident.moveTo(to);
        incidents.save(incident);
        transitions.save(StateTransition.of(incident, from, event, actor, reason));
        return incident;
    }

    @Transactional(readOnly = true)
    public Incident get(String tenantId, UUID incidentId) {
        return incidents.findByIdAndTenantId(incidentId, tenantId)
        .orElseThrow(() -> new IncidentExceptions.NotFound(incidentId));
    }

    @Transactional(readOnly = true)
    public List<Incident> list(String tenantId) {
        return incidents.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    @Transactional(readOnly = true)
    public List<StateTransition> auditTrail(String tenantId, UUID incidentId) {
        get(tenantId, incidentId);
        return transitions.findByIncidentIdAndTenantIdOrderByTimestampAsc(incidentId, tenantId);
    }

}
