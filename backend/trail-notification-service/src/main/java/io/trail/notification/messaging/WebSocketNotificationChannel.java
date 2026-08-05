package io.trail.notification.messaging;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import io.trail.events.IncidentEvent;

@Component
public class WebSocketNotificationChannel implements NotificationChannel {

    private final SimpMessagingTemplate messaging;

    public WebSocketNotificationChannel(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @Override
    public void deliver(IncidentEvent event) {
        messaging.convertAndSend("/topic/incidents/" + event.tenantId(), event);
    }

}
