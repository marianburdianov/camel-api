package com.mburdianov.camel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mburdianov.camel.api.routes.EmployeeRoutes;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class EmployeeRoutesTest extends CamelTestSupport {

    private String testRoute = "";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new EmployeeRoutes();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        context.setMessageHistory(true);
        context.addComponent("sql", new MockComponent());
        return context;
    }

    @Test
    public void getAllEmployeesTest() throws Exception {
        testRoute = "direct:get-employees";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.start();
                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:/from-db/employees/get-all-employees-resultset.json", List.class))
                ;
                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");
        template.sendBody(testRoute, "");

        Exchange responseBody = mockResult.getExchanges().get(0);
        ArrayList<Object> employees = new ObjectMapper().readValue(responseBody.getMessage().getBody(String.class), ArrayList.class);

        ArrayList<Object> expectedEmployees =
                new ObjectMapper().readValue(new ClassPathResource("responses/employees/get-all-employees-response.json").getFile(), ArrayList.class);
        System.out.println(expectedEmployees);

        assertEquals(5, employees.size());
        assertEquals(expectedEmployees, employees);

    }

    @Test
    public void getEmployeeByIdTest() throws Exception {
        testRoute = "direct:get-employee-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:/from-db/employees/get-employee-by-id-resultset.json"))
                ;
                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");
        template.sendBody(testRoute, "");

        Exchange responseBody = mockResult.getExchanges().get(0);

        Object employee = new ObjectMapper().readValue(responseBody.getMessage().getBody(String.class), Object.class);

        Object expectedEmployee = new ObjectMapper().readValue(new ClassPathResource("responses/employees/get-employee-by-id-response.json").getFile(), Object.class);

        Assertions.assertEquals(expectedEmployee, employee);
    }

    @Test
    public void createEmployeeTest() throws Exception {

        testRoute = "direct:post-employee";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                weaveAddFirst().setBody(datasonnet("resource:classpath:/requests/employees/create-employee-request.json"));

                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(simple(""))
                ;

                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");

        template.sendBody(testRoute, "");

        Exchange exchange = mockResult.getExchanges().get(0);

        String statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString();

        assertThat(statusCode).isEqualTo("201");
    }

    @Test
    public void deleteEmployeeTest() throws Exception {

        testRoute = "direct:delete-employee-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                weaveByToString(".*get-employee-by-id.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/employees/get-employee-by-id-response.json"))
                ;

                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(simple(""))
                ;

                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");

        template.sendBody(testRoute, "");

        Exchange exchange = mockResult.getExchanges().get(0);

        Integer statusCode = exchange.getMessage().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);

        assertThat(statusCode).isEqualTo(200);
    }

    @Test
    public void updateEmployeeTest() throws Exception {

        testRoute = "direct:update-employee-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {

            @Override
            public void configure() throws Exception {

                weaveAddFirst().setBody(datasonnet("resource:classpath:/requests/employees/update-employee-request.json"));

                weaveByToString(".*get-employee-by-id.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/employees/get-employee-by-id-response.json"))
                ;

                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(simple(""))
                ;

                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");

        template.sendBody(testRoute, "");

        Exchange exchange = mockResult.getExchanges().get(0);
        String statusCode = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE).toString();

        assertThat(statusCode).isEqualTo("200");
    }

}
