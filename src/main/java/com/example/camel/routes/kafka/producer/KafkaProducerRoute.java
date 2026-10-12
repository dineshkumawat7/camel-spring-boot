package com.example.camel.routes.kafka.producer;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class KafkaProducerRoute extends RouteBuilder {
    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .log(LoggingLevel.ERROR, "Error occurred while processing route: ${exception.message}\n${exception.stacktrace}")
                .process(exchange -> {
                    String stackTrace = exchange.getContext()
                            .resolveLanguage("simple")
                            .createExpression("${exception.stacktrace}")
                            .evaluate(exchange, String.class);

                    exchange.getMessage().setBody(Map.of(
                            "status", "error",
                            "message", "Failed to send message to Kafka!",
                            "error", (stackTrace != null && !stackTrace.isBlank())
                                    ? stackTrace
                                    : "Unknown error occurred."
                    ));
                });

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/kafka")
                .post("/send")
                .to("direct:kafka-producer-route");

        from("direct:kafka-producer-route")
                .routeId("kafka-producer-route")
                .process(exchange -> exchange.getMessage().setHeader(KafkaConstants.KEY, UUID.randomUUID().toString()))
                .log("Sending message to Kafka with Key: ${header.CamelKafkaKey} | Body: ${body}")
                .to("kafka:{{camel.component.kafka.topic}}")
                .setBody(constant(Map.of(
                        "status", "success",
                        "message", "Successfully sent message to Kafka!"
                )));
    }
}
