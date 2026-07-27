package io.trail.incident.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.trail.incident.domain.Incident;
import io.trail.incident.service.IncidentService;
import io.trail.incident.web.dto.CreateIncidentRequest;
import io.trail.incident.web.dto.IncidentResponse;
import io.trail.incident.web.dto.TransitionRequest;
import io.trail.incident.web.dto.TransitionResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentResponse create(@RequestHeader("X-Tenant-Id") String tenantId,
                                   @RequestHeader(value = "X-Actor", defaultValue = "system") String actor,
                                   @Valid @RequestBody CreateIncidentRequest request) {
        Incident incident = service.create(
                tenantId, request.title(), request.description(), request.source(), actor);
        return IncidentResponse.from(incident);
    }

    @PostMapping("/{id}/transitions")
    public IncidentResponse transition(@RequestHeader("X-Tenant-Id") String tenantId,
                                       @RequestHeader(value = "X-Actor", defaultValue = "system") String actor,
                                       @PathVariable UUID id,
                                       @Valid @RequestBody TransitionRequest request) {
        Incident incident = service.transition(
                tenantId, id, request.event(), actor, request.reason());
        return IncidentResponse.from(incident);
    }

    @GetMapping("/{id}")
    public IncidentResponse get(@RequestHeader("X-Tenant-Id") String tenantId,
                                @PathVariable UUID id) {
        return IncidentResponse.from(service.get(tenantId, id));
    }
    
    @GetMapping
    public List<IncidentResponse> list(@RequestHeader("X-Tenant-Id") String tenantId) {
        return service.list(tenantId).stream().map(IncidentResponse::from).toList();
    }

    @GetMapping("/{id}/transitions")
    public List<TransitionResponse> auditTrail(@RequestHeader("X-Tenant-Id") String tenantId,
                                               @PathVariable UUID id) {
        return service.auditTrail(tenantId, id).stream().map(TransitionResponse::from).toList();
    }

}