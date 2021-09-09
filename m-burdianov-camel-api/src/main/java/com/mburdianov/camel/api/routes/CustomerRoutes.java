package com.mburdianov.camel.api.routes;

import com.datasonnet.document.MediaTypes;
import com.mburdianov.camel.api.BaseRestRouteBuilder;
import com.mburdianov.camel.api.exceptions.ResourceNotFound;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * The RoutesImplementation class holds implementations for the end points configured in RoutesGenerated.
 * These routes are based on operation Ids, that correspond to an API end point:  method+path.
 *
 * @author Maven Archetype (camel-openapi-archetype)
 */

@Component
public class CustomerRoutes extends BaseRestRouteBuilder {

    @Override
    public void configure() throws Exception {
        super.configure();

        from(direct("get-customers"))
                .routeId("direct:get-customers")
                .to(sql("classpath:/sql/customers/get-all-customers.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("resource:classpath:/formats/customers/get-all-customers.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("post-customer"))
                .routeId("direct:post-customer")
                .setProperty("firstName", datasonnetEx("payload.firstName", String.class))
                .setProperty("lastName", datasonnetEx("payload.lastName", String.class))
                .setProperty("phoneNumber", datasonnetEx("payload.phoneNumber", String.class))
                .setProperty("address", datasonnetEx("payload.address", String.class))
                .transform(datasonnetEx("resource:classpath:/formats/customers/create-customer.ds", String.class))
                .to(sql("classpath:/sql/customers/create-customer.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .transform(datasonnetEx("{'message': 'Created'}", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("get-customer-by-id"))
                .routeId("direct:get-customer-by-id")
                .to(sql("classpath:/sql/customers/get-customer-by-id.sql"))
                .choice()
                .when(simple("${header.CamelSqlRowCount} < 1"))
                .throwException(new ResourceNotFound())
                .end()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("resource:classpath:/formats/customers/get-customer-by-id.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("update-customer-by-id"))
                .routeId("direct:update-customer-by-id")
                .setProperty("firstName", datasonnetEx("payload.firstName", String.class))
                .setProperty("lastName", datasonnetEx("payload.lastName", String.class))
                .setProperty("phoneNumber", datasonnetEx("payload.phoneNumber", String.class))
                .setProperty("address", datasonnetEx("payload.address", String.class))
                .transform(datasonnetEx("resource:classpath:/formats/customers/update-customer-by-id.ds", String.class))
                .to(direct("get-customer-by-id"))
                .to(sql("classpath:/sql/customers/update-customer-by-id.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("{'message': 'Updated'}", String.class)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("delete-customer-by-id"))
                .routeId("direct:delete-customer-by-id")
                .to(direct("get-customer-by-id"))
                .to(sql("classpath:/sql/customers/delete-customer-by-id.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("{'message': 'Deleted'}", String.class)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
    }
}
