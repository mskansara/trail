package io.trail.incident;

import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.trail.incident.domain.Incident;
import io.trail.incident.domain.IncidentEvent;
import io.trail.incident.domain.IncidentState;
import io.trail.incident.domain.StateTransition;
import io.trail.incident.service.IncidentExceptions;
import io.trail.incident.service.IncidentService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;



@SpringBootTest
@Testcontainers
class IncidentLifecycleIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
        
    @Autowired
    private IncidentService service;

    private static final String TENANT_A = "tenant-a";
    private static final String TENANT_B = "tenant-b";

    @Test
    void fullLifecycleRunsAndIsFullyAudited() {
        Incident incident = service.create(
                TENANT_A, "payments-api latency", "p99 above 4s", "prometheus", "alice");
        UUID id = incident.getId();

        assertThat(incident.getState()).isEqualTo(IncidentState.OPEN);
        assertThat(incident.getSeverity()).isNull();

        service.transition(TENANT_A, id, IncidentEvent.ACKNOWLEDGE, "alice", "on it");
        service.transition(TENANT_A, id, IncidentEvent.INVESTIGATE, "alice", "checking deploys");
        service.transition(TENANT_A, id, IncidentEvent.RESOLVE, "bob", "rolled back");
        Incident closed = service.transition(TENANT_A, id, IncidentEvent.CLOSE, "bob", "confirmed healthy");

        assertThat(closed.getState()).isEqualTo(IncidentState.CLOSED);
        assertThat(closed.getResolvedAt()).isNotNull();

        List<StateTransition> trail = service.auditTrail(TENANT_A, id);
        assertThat(trail).hasSize(5);
        assertThat(trail).extracting(StateTransition::getToState).containsExactly(
                IncidentState.OPEN,
                IncidentState.ACKNOWLEDGED,
                IncidentState.INVESTIGATING,
                IncidentState.RESOLVED,
                IncidentState.CLOSED);
    }

    @Test
    void illegalTransitionIsRejectedAndPersistsNothing() {
        Incident incident = service.create(
                TENANT_A, "cache miss storm", null, "datadog", "alice");
        UUID id = incident.getId();

        // RESOLVE is illegal from OPEN.
        assertThatThrownBy(() ->
                service.transition(TENANT_A, id, IncidentEvent.RESOLVE, "alice", "nope"))
                .isInstanceOf(IncidentExceptions.IllegalTransition.class);

        // State unchanged, and NO stray audit row was written (I3).
        assertThat(service.get(TENANT_A, id).getState()).isEqualTo(IncidentState.OPEN);
        assertThat(service.auditTrail(TENANT_A, id)).hasSize(1);   // only the creation row
    }

    @Test
    void reopenReturnsResolvedIncidentToInvestigating() {
        Incident incident = service.create(TENANT_A, "queue backlog", null, "manual", "alice");
        UUID id = incident.getId();
        service.transition(TENANT_A, id, IncidentEvent.ACKNOWLEDGE, "alice", null);
        service.transition(TENANT_A, id, IncidentEvent.INVESTIGATE, "alice", null);
        service.transition(TENANT_A, id, IncidentEvent.RESOLVE, "alice", "drained");

        Incident reopened = service.transition(
                TENANT_A, id, IncidentEvent.REOPEN, "alice", "backlog returned");

        assertThat(reopened.getState()).isEqualTo(IncidentState.INVESTIGATING);
    }

    @Test
    void oneTenantCannotSeeAnothersIncident() {
        Incident a = service.create(TENANT_A, "A's incident", null, "manual", "alice");

        // Tenant B presenting A's id must not resolve to an incident (I7).
        assertThatThrownBy(() -> service.get(TENANT_B, a.getId()))
                .isInstanceOf(IncidentExceptions.NotFound.class);
    }

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
