package com.example.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestRoute extends RouteBuilder {
    @Override
    public void configure() {

        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/test")
                .get()
                .to("direct:test-route");

        from("direct:test-route")
                .setBody(constant(Map.of(
                        "status", "success",
                        "message", "Hello from Apache Camel REST API!"
                )));
    }
}
