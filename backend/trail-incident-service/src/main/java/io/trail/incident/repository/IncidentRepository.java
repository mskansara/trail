package io.trail.incident.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import io.trail.incident.domain.Incident;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {

    Optional<Incident> findByIdAndTenantId(UUID id, String tenantId);

    List<Incident> findByTenantIdOrderByCreatedAtDesc(String tenantId);
}
