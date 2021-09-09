package com.mburdianov.camel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mburdianov.camel.api.routes.PurchaseInfoRoutes;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockComponent;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PurchaseInfoRoutesTest extends CamelTestSupport {

    private String testRoute = "";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new PurchaseInfoRoutes();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = super.createCamelContext();
        context.setMessageHistory(true);
        context.addComponent("sql", new MockComponent());

        Properties props = new Properties();
        props.put("camel.rest.context-path", "/api/");
        PropertiesComponent propertiesComponent = new PropertiesComponent();
        propertiesComponent.setOverrideProperties(props);
        context.setPropertiesComponent(propertiesComponent);

        return context;
    }

    @Test
    public void getAllPurchaseInfoTest() throws Exception {
        testRoute = "direct:get-all-purchaseinfo";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.start();
                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:/from-db/purchaseInfo/get-all-purchaseinfo-resultset.json", List.class))
                ;
                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");
        template.sendBody(testRoute, "");

        Exchange responseBody = mockResult.getExchanges().get(0);
        ArrayList<Object> products = new ObjectMapper().readValue(responseBody.getMessage().getBody(String.class), ArrayList.class);

        ArrayList<Object> expectedProducts =
                new ObjectMapper().readValue(new ClassPathResource("responses/purchaseInfo/get-all-purchaseinfo-response.json").getFile(), ArrayList.class);
        System.out.println(expectedProducts);

        assertEquals(10, products.size());
        assertEquals(expectedProducts, products);

    }

    @Test
    public void getPurchaseInfoByIdTest() throws Exception {
        testRoute = "direct:get-purchaseinfo-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:/from-db/purchaseInfo/get-purchaseinfo-by-id-resultset.json"))
                ;
                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");
        template.sendBody(testRoute, "");

        Exchange responseBody = mockResult.getExchanges().get(0);

        Object product = new ObjectMapper().readValue(responseBody.getMessage().getBody(String.class), Object.class);

        Object expectedProduct = new ObjectMapper().readValue(new ClassPathResource("responses/purchaseInfo/get-purchaseinfo-by-id-response.json").getFile(), Object.class);

        assertEquals(expectedProduct, product);
    }

    @Test
    public void createPurchaseInfoTest() throws Exception {

        testRoute = "direct:post-purchaseinfo";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                weaveAddFirst().setBody(datasonnet("resource:classpath:/requests/purchaseInfo/create-purchaseinfo-request.json"));

                weaveByToString(".*employee-exists.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/employees/get-employee-by-id-response.json"))
                ;

                weaveByToString(".*customer-exists.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/customers/get-customer-by-id-response.json"))
                ;

                weaveByToString(".*product-exists.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/products/get-product-by-id-response.json"))
                ;

                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(simple(""))
                ;

                weaveByToString(".*insert-into-purchaseinfos-products-for-create.*")
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
    public void deletePurchaseInfoTest() throws Exception {

        testRoute = "direct:delete-purchaseinfo-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                weaveByToString(".*get-purchaseinfo-by-id.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/purchaseInfo/get-purchaseinfo-by-id-response.json"))
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
    public void updatePurchaseInfoTest() throws Exception {

        testRoute = "direct:update-purchaseinfo-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {

            @Override
            public void configure() throws Exception {

                weaveAddFirst().setBody(datasonnet("resource:classpath:/requests/purchaseInfo/update-purchaseinfo-request.json"));

                weaveByToString(".*purchaseinfo-exists.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/purchaseInfo/get-purchaseinfo-by-id-response.json"))
                ;

                weaveByToString(".*employee-exists.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/employees/get-employee-by-id-response.json"))
                ;

                weaveByToString(".*customer-exists.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/customers/get-customer-by-id-response.json"))
                ;

                weaveByToString(".*product-exists.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/products/get-product-by-id-response.json"))
                ;

                weaveByToString(".*delete-all-products.*")
                        .replace()
                        .setBody(simple(""))
                ;

                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(simple(""))
                ;

                weaveByToString(".*insert-into-purchaseinfos-products.*")
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
