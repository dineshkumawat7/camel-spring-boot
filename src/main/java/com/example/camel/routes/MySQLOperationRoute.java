package com.example.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MySQLOperationRoute extends RouteBuilder {
    private static final String QUERY_SELECT_ALL = "SELECT * FROM users";
    @Override
    public void configure() {
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/users")
                .get()
                .description("Fetch all users from MySQL")
                .outType(List.class)
                .to("direct:fetch-all-users-route");

        from("direct:fetch-all-users-route")
                .routeId("fetch-all-users-rest")
                .to("sql:" + QUERY_SELECT_ALL)
                .log("Fetched ${body.size()} users for REST response");
    }
}
