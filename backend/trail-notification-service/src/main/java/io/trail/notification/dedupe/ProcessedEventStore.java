package io.trail.notification.dedupe;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Component;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Component
public class ProcessedEventStore {
    private final DynamoDbClient dynamo;

    public ProcessedEventStore(DynamoDbClient dynamo) {
        this.dynamo = dynamo;
    }
    /**
     * @return true if this eventId was newly claimed (first time — proceed),
     *         false if it was already processed (duplicate — skip).
     */
    public boolean claim(String eventId) {
        try {
            dynamo.putItem(PutItemRequest.builder()
                    .tableName(DynamoConfig.TABLE)
                    .item(Map.of(
                            "eventId", AttributeValue.fromS(eventId),
                            "processedAt", AttributeValue.fromS(Instant.now().toString())))
                    .conditionExpression("attribute_not_exists(eventId)")  // only if new
                    .build());
            return true;   // insert succeeded → first time seeing this event
        } catch (ConditionalCheckFailedException duplicate) {
            return false;  // item already existed → we've handled this event before
        }
    }
}
