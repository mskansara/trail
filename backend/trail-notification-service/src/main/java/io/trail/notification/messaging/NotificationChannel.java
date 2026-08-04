package io.trail.notification.messaging;

import io.trail.events.IncidentEvent;
public interface NotificationChannel {
    void deliver(IncidentEvent event);
}
