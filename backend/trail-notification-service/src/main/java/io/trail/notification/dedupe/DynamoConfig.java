package io.trail.notification.dedupe;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Configuration
public class DynamoConfig {

    public static final String TABLE = "processed_events";

    @Value("${dynamo.endpoint:http://localhost:8000}")
    private String endpoint;

    @Bean
    DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("local", "local"))) 
                .build();
    }
    @Bean
    TableInitializer tableInitializer(DynamoDbClient client) {
        return new TableInitializer(client);
    }

    static class TableInitializer {
        private final DynamoDbClient client;
        TableInitializer(DynamoDbClient client) { this.client = client; }

        @PostConstruct
        void createTableIfMissing() {
            try {
                client.describeTable(DescribeTableRequest.builder().tableName(TABLE).build());
            } catch (ResourceNotFoundException e) {
                client.createTable(CreateTableRequest.builder()
                        .tableName(TABLE)
                        .keySchema(KeySchemaElement.builder()
                                .attributeName("eventId").keyType(KeyType.HASH).build())
                        .attributeDefinitions(AttributeDefinition.builder()
                                .attributeName("eventId").attributeType(ScalarAttributeType.S).build())
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .build());
            }
        }
    }

}
