package com.mburdianov.camel.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mburdianov.camel.api.routes.ProductRoutes;
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

public class ProductRoutesTest extends CamelTestSupport {

    private String testRoute = "";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new ProductRoutes();
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
    public void getAllProductsTest() throws Exception {
        testRoute = "direct:get-products";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                context.start();
                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:/from-db/products/get-all-products-resultset.json", List.class))
                ;
                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");
        template.sendBody(testRoute, "");

        Exchange responseBody = mockResult.getExchanges().get(0);
        ArrayList<Object> products = new ObjectMapper().readValue(responseBody.getMessage().getBody(String.class), ArrayList.class);

        ArrayList<Object> expectedProducts =
                new ObjectMapper().readValue(new ClassPathResource("responses/products/get-all-products-response.json").getFile(), ArrayList.class);
        System.out.println(expectedProducts);

        assertEquals(5, products.size());
        assertEquals(expectedProducts, products);

    }

    @Test
    public void getProductByIdTest() throws Exception {
        testRoute = "direct:get-product-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                weaveByToString(".*sql.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:/from-db/products/get-product-by-id-resultset.json"))
                ;
                weaveAddLast().to("mock:result");
            }
        });

        MockEndpoint mockResult = getMockEndpoint("mock:result");
        template.sendBody(testRoute, "");

        Exchange responseBody = mockResult.getExchanges().get(0);

        Object product = new ObjectMapper().readValue(responseBody.getMessage().getBody(String.class), Object.class);

        Object expectedProduct = new ObjectMapper().readValue(new ClassPathResource("responses/products/get-product-by-id-response.json").getFile(), Object.class);

        assertEquals(expectedProduct, product);
    }

    @Test
    public void createProductTest() throws Exception {

        testRoute = "direct:post-product";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                weaveAddFirst().setBody(datasonnet("resource:classpath:/requests/products/create-product-request.json"));

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
    public void deleteProductTest() throws Exception {

        testRoute = "direct:delete-product-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {

                weaveByToString(".*get-product-by-id.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/products/get-product-by-id-response.json"))
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
    public void updateProductTest() throws Exception {

        testRoute = "direct:update-product-by-id";

        AdviceWith.adviceWith(context.getRouteDefinition(testRoute), context, new AdviceWithRouteBuilder() {

            @Override
            public void configure() throws Exception {

                weaveAddFirst().setBody(datasonnet("resource:classpath:/requests/products/update-product-request.json"));

                weaveByToString(".*get-product-by-id.*")
                        .replace()
                        .setBody(datasonnet("resource:classpath:responses/products/get-product-by-id-response.json"))
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
