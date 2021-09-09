package com.mburdianov.camel.api;

import javax.annotation.Generated;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ms3_inc.tavros.extensions.rest.OpenApi4jValidator;

/**
 * Generated routes are based on the OpenAPI document in src/generated/api folder.
 *
 * @author Maven Archetype (camel-oas-archetype)
 */
@Generated("com.ms3_inc.camel.archetype.oas")
@Component
public class RoutesGenerated extends BaseRestRouteBuilder {
    private final String contextPath;

    public RoutesGenerated(@Value("${camel.rest.context-path}") String contextPath) {
        super();
        this.contextPath = contextPath;
    }

    /**
     * Defines Apache Camel routes using the OpenAPI REST DSL.
     * Routes are built using a get(PATH) rest message processor.
     *
     * Make changes to this file with caution.
     * If the API specification changes and this file is regenerated,
     * previous changes may be overwritten.
     */
    @Override
    public void configure() throws Exception {
        super.configure();

        restConfiguration().component("undertow");

        interceptFrom()
            .process(new OpenApi4jValidator("camel-api.yaml", contextPath));

        rest()
            .get("/employees")
                .id("get-employees")
                .produces("application/json")
                .to(direct("get-employees").getUri())
            .post("/employees")
                .id("post-employee")
                .consumes("application/json")
                .to(direct("post-employee").getUri())
            .get("/employees/{employeeId}")
                .id("get-employee-by-id")
                .produces("application/json")
                .to(direct("get-employee-by-id").getUri())
            .put("/employees/{employeeId}")
                .id("update-employee-by-id")
                .consumes("application/json")
                .to(direct("update-employee-by-id").getUri())
            .delete("/employees/{employeeId}")
                .id("delete-employee-by-id")
                .produces("application/json")
                .to(direct("delete-employee-by-id").getUri())
            .get("/products")
                .id("get-products")
                .produces("application/json")
                .to(direct("get-products").getUri())
            .post("/products")
                .id("post-product")
                .consumes("application/json")
                .to(direct("post-product").getUri())
            .get("/products/{productId}")
                .id("get-product-by-id")
                .produces("application/json")
                .to(direct("get-product-by-id").getUri())
            .put("/products/{productId}")
                .id("update-product-by-id")
                .consumes("application/json")
                .to(direct("update-product-by-id").getUri())
            .delete("/products/{productId}")
                .id("delete-product-by-id")
                .produces("application/json")
                .to(direct("delete-product-by-id").getUri())
            .get("/customers")
                .id("get-customers")
                .produces("application/json")
                .to(direct("get-customers").getUri())
            .post("/customers")
                .id("post-customer")
                .consumes("application/json")
                .to(direct("post-customer").getUri())
            .get("/customers/{customerId}")
                .id("get-customer-by-id")
                .produces("application/json")
                .to(direct("get-customer-by-id").getUri())
            .put("/customers/{customerId}")
                .id("update-customer-by-id")
                .consumes("application/json")
                .to(direct("update-customer-by-id").getUri())
            .delete("/customers/{customerId}")
                .id("delete-customer-by-id")
                .produces("application/json")
                .to(direct("delete-customer-by-id").getUri())
            .get("/purchaseInfo")
                .id("get-all-purchaseinfo")
                .produces("application/json")
                .to(direct("get-all-purchaseinfo").getUri())
            .post("/purchaseInfo")
                .id("post-purchaseinfo")
                .consumes("application/json")
                .to(direct("post-purchaseinfo").getUri())
            .get("/purchaseInfo/{purchaseInfoId}")
                .id("get-purchaseinfo-by-id")
                .produces("application/json")
                .to(direct("get-purchaseinfo-by-id").getUri())
            .put("/purchaseInfo/{purchaseInfoId}")
                .id("update-purchaseinfo-by-id")
                .consumes("application/json")
                .to(direct("update-purchaseinfo-by-id").getUri())
            .delete("/purchaseInfo/{purchaseInfoId}")
                .id("delete-purchaseinfo-by-id")
                .produces("application/json")
                .to(direct("delete-purchaseinfo-by-id").getUri())
        ;
    }
}
