package io.trail.incident.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.trail.incident.domain.StateTransition;

public interface StateTransitionRepository extends JpaRepository<StateTransition, UUID> {
    List<StateTransition> findByIncidentIdAndTenantIdOrderByTimestampAsc(UUID incidentId, String tenantId);
}
