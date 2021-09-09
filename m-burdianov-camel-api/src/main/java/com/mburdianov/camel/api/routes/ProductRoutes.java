package com.mburdianov.camel.api.routes;

import com.datasonnet.document.MediaTypes;
import com.mburdianov.camel.api.BaseRestRouteBuilder;
import com.mburdianov.camel.api.exceptions.ResourceNotFound;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component
public class ProductRoutes extends BaseRestRouteBuilder {

    @Override
    public void configure() throws Exception {
        super.configure();

        from(direct("get-products"))
                .routeId("direct:get-products")
                .to(sql("classpath:/sql/products/get-all-products.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("resource:classpath:/formats/products/get-all-products.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("post-product"))
                .routeId("direct:post-product")
                .to("log:like-to-see-all?level=INFO&showAll=true&multiline=true")
                .setProperty("name", datasonnetEx("payload.name", String.class))
                .setProperty("price", datasonnetEx("payload.price", String.class))
                .setProperty("quantity", datasonnetEx("payload.quantity", String.class))
                .transform(datasonnet("resource:classpath:/formats/products/create-product.ds", String.class))
                .to(sql("classpath:/sql/products/create-product.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .transform(datasonnetEx("{'message': 'Created'}", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("get-product-by-id"))
                .routeId("direct:get-product-by-id")
                .to(sql("classpath:/sql/products/get-product-by-id.sql"))
                .choice()
                    .when(simple("${header.CamelSqlRowCount} < 1"))
                        .throwException(new ResourceNotFound())
                .end()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("resource:classpath:/formats/products/get-product-by-id.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("update-product-by-id"))
                .routeId("direct:update-product-by-id")
                .setProperty("name", datasonnetEx("payload.name", String.class))
                .setProperty("price", datasonnetEx("payload.price", String.class))
                .setProperty("quantity", datasonnetEx("payload.quantity", String.class))
                .transform(datasonnetEx("resource:classpath:/formats/products/update-product-by-id.ds", String.class))
                .to(direct("get-product-by-id"))
                .to(sql("classpath:/sql/products/update-product-by-id.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("{'message': 'Updated'}", String.class)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("delete-product-by-id"))
                .routeId("direct:delete-product-by-id")
                .to(direct("get-product-by-id"))
                .to(sql("classpath:/sql/products/delete-product-by-id.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("{'message': 'Deleted'}", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;

    }
}
