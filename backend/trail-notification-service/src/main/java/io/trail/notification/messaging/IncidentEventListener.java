package io.trail.notification.messaging;

import io.trail.events.IncidentEvent;
import io.trail.events.Topics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
@Component
public class IncidentEventListener {
    private static final Logger log = LoggerFactory.getLogger(IncidentEventListener.class);
    private final NotificationChannel channel;

    public IncidentEventListener(NotificationChannel channel) {
        this.channel = channel;
    }
    
    @KafkaListener(topics = Topics.INCIDENT_EVENTS, groupId = "trail-notification")
    public void onEvent(IncidentEvent event) {
        log.info("consumed event {} ({})", event.eventId(), event.type());
        channel.deliver(event);
    }
}
