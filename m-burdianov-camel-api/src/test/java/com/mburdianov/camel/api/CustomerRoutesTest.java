package com.mburdianov.camel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mburdianov.camel.api.routes.CustomerRoutes;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerRoutesTest extends CamelTestSupport {

    private String testRoute = "";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new CustomerRoutes();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        context.setMessageHistory(true);
        context.addComponent("sql", new MockComponent());
        return context;
    }

    @Test
    public void getAllCustomersTest() throws Exception {
        testRoute = "direct:get-customers";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.start();
                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:/from-db/customers/get-all-customers-resultset.json", List.class))
                ;
                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");
        template.sendBody(testRoute, "");

        Exchange responseBody = mockResult.getExchanges().get(0);
        ArrayList<Object> customers = new ObjectMapper().readValue(responseBody.getMessage().getBody(String.class), ArrayList.class);

        ArrayList<Object> expectedCustomers =
                new ObjectMapper().readValue(new ClassPathResource("responses/customers/get-all-customers-response.json").getFile(), ArrayList.class);
        System.out.println(expectedCustomers);

        assertEquals(5, customers.size());
        assertEquals(expectedCustomers, customers);
    }

    @Test
    public void getCustomerByIdTest() throws Exception {
        testRoute = "direct:get-customer-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:/from-db/customers/get-customer-by-id-resultset.json"))
                ;
                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");
        template.sendBody(testRoute, "");

        Exchange responseBody = mockResult.getExchanges().get(0);

        Object customer = new ObjectMapper().readValue(responseBody.getMessage().getBody(String.class), Object.class);

        Object expectedCustomer = new ObjectMapper().readValue(new ClassPathResource("responses/customers/get-customer-by-id-response.json").getFile(), Object.class);

        assertEquals(expectedCustomer, customer);
    }

    @Test
    public void createCustomerTest() throws Exception {

        testRoute = "direct:post-customer";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                weaveAddFirst().setBody(datasonnet("resource:classpath:/requests/customers/create-customer-request.json"));

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
    public void deleteCustomerTest() throws Exception {

        testRoute = "direct:delete-customer-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                weaveByToString(".*get-customer-by-id.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/customers/get-customer-by-id-response.json"))
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
    public void updateCustomerTest() throws Exception {

        testRoute = "direct:update-customer-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {

            @Override
            public void configure() throws Exception {

                weaveAddFirst().setBody(datasonnet("resource:classpath:/requests/customers/update-customer-request.json"));

                weaveByToString(".*get-customer-by-id.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/customers/get-customer-by-id-response.json"))
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
