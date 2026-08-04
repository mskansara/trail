package io.trail.incident.messaging;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import io.trail.events.IncidentEvent;
import io.trail.events.Topics;

@Component
public class IncidentEventPublisher {

    private final KafkaTemplate<String, IncidentEvent> kafka;

    public IncidentEventPublisher(KafkaTemplate<String, IncidentEvent> kafka) {
        this.kafka = kafka;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(IncidentEvent event) {
        kafka.send(Topics.INCIDENT_EVENTS, event.incidentId().toString(), event);
    }
}
