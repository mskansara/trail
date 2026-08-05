package io.trail.notification.messaging;

import io.trail.events.IncidentEvent;
import io.trail.events.Topics;
import io.trail.notification.dedupe.ProcessedEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class IncidentEventListener {

    private static final Logger log = LoggerFactory.getLogger(IncidentEventListener.class);
    private final NotificationChannel channel;
    private final ProcessedEventStore processed;

    public IncidentEventListener(NotificationChannel channel, ProcessedEventStore processed) {
        this.channel = channel;
        this.processed = processed;
    }

    @KafkaListener(topics = Topics.INCIDENT_EVENTS, groupId = "trail-notification")
    public void onEvent(IncidentEvent event) {
        if (!processed.claim(event.eventId().toString())) {
            log.info("skipping duplicate event {}", event.eventId());
            return;
        }
        log.info("consumed event {} ({})", event.eventId(), event.type());
        channel.deliver(event);
    }
}