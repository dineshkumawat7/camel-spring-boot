package com.example.camel.routes.kafka.consumer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerRoute extends RouteBuilder {
    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .log("Error occurred while consuming message from Kafka: ${exception.message}\n${exception.stacktrace}");

        from("kafka:{{camel.component.kafka.topic}}")
                .routeId("kafka-consumer-route")
                .log("Consumed message from Kafka -> Key: ${header.CamelKafkaKey} | Body: ${body}");
    }
}
