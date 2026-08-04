package io.trail.notification.messaging;

import io.trail.events.IncidentEvent;
import io.trail.events.IncidentEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingNotificationChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationChannel.class);
    @Override
    public void deliver(IncidentEvent event) {
        if (event.type() == IncidentEventType.CREATED) {
            log.info("[NOTIFY] new incident {} for tenant {} — {} (from {})",
                    event.incidentId(), event.tenantId(), event.title(), event.source());
        } else if (event.type() == IncidentEventType.STATE_CHANGED) {
            log.info("[NOTIFY] incident {} moved {} → {}",
                    event.incidentId(), event.fromState(), event.toState());
        } else {
            log.info("[NOTIFY] incident {} event {}", event.incidentId(), event.type());
        }
    }

}
