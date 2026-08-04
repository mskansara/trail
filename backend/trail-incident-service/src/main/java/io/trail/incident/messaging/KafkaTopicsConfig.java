package io.trail.incident.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import io.trail.events.Topics;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    NewTopic incidentEvents() {
        return TopicBuilder.name(Topics.INCIDENT_EVENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
