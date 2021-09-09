package com.mburdianov.camel.api.routes;

import com.datasonnet.document.MediaTypes;
import com.mburdianov.camel.api.BaseRestRouteBuilder;
import com.mburdianov.camel.api.exceptions.NestedResourceNotFound;
import com.mburdianov.camel.api.exceptions.ResourceNotFound;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PurchaseInfoRoutes extends BaseRestRouteBuilder {

    @Override
    public void configure() throws Exception {
        super.configure();

        from(direct("get-all-purchaseinfo"))
                .routeId("direct:get-all-purchaseinfo")
                .to(sql("classpath:/sql/purchaseInfo/get-all-purchaseInfo.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("resource:classpath:/formats/purchase-info/get-all-purchaseinfo.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("post-purchaseinfo"))
                .routeId("direct:post-purchaseinfo")
                .setProperty("employeeId", datasonnetEx("payload.employeeId", String.class))
                .setProperty("customerId", datasonnetEx("payload.customerId", String.class))
                .setProperty("cost", datasonnetEx("payload.cost", String.class))
                .setProperty("amount", datasonnetEx("payload.amount", String.class))
                .setProperty("date", datasonnetEx("payload.date", String.class))

                .setProperty("listOfProducts", datasonnetEx("payload.productId", List.class))
                .setProperty("numberOfElements", datasonnetEx("ds.arrays.countBy(cml.exchangeProperty('listOfProducts'), function(item) item != null)", Integer.class))

                .transform(datasonnetEx("resource:classpath:/formats/purchase-info/create-purchaseinfo.ds", String.class))

                .to(direct("employee-exists"))
                .to(direct("customer-exists"))
                .to(direct("product-exists"))

                .setHeader("CamelSqlRetrieveGeneratedKeys", constant(true))

                .to(sql("classpath:/sql/purchaseInfo/create-purchaseInfo.sql"))

                .setProperty("lastPurchaseInfoId", simple("${headers.CamelSqlGeneratedKeyRows[0]['GENERATED_KEY']}"))

                .to(direct("insert-into-purchaseinfos-products-for-create"))

                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .transform(datasonnetEx("{'message': 'Created'}", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("get-purchaseinfo-by-id"))
                .routeId("direct:get-purchaseinfo-by-id")
                .to(sql("classpath:/sql/purchaseInfo/get-purchaseInfo-by-id.sql"))
                .choice()
                    .when(simple("${header.CamelSqlRowCount} < 1"))
                    .throwException(new ResourceNotFound())
                .end()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("resource:classpath:/formats/purchase-info/get-purchaseinfo-by-id.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("update-purchaseinfo-by-id"))
                .routeId("direct:update-purchaseinfo-by-id")
                .setProperty("employeeId", datasonnetEx("payload.employeeId", String.class))
                .setProperty("customerId", datasonnetEx("payload.customerId", String.class))
                .setProperty("cost", datasonnetEx("payload.cost", String.class))
                .setProperty("amount", datasonnetEx("payload.amount", String.class))
                .setProperty("date", datasonnetEx("payload.date", String.class))

                .setProperty("listOfProducts", datasonnetEx("payload.productId", List.class))
                .setProperty("numberOfElements", datasonnetEx("ds.arrays.countBy(cml.exchangeProperty('listOfProducts'), function(item) item != null)", Integer.class))

                .transform(datasonnetEx("resource:classpath:/formats/purchase-info/update-purchaseinfo-by-id.ds", String.class))
                .to(direct("purchaseinfo-exists"))
                .to(direct("employee-exists"))
                .to(direct("customer-exists"))
                .to(direct("product-exists"))
                .to(direct("delete-all-products"))

                .to(sql("classpath:/sql/purchaseInfo/update-purchaseInfo-by-id.sql"))

                .to(direct("insert-into-purchaseinfos-products"))

                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("{'message': 'Updated'}", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("delete-purchaseinfo-by-id"))
                .routeId("direct:delete-purchaseinfo-by-id")
                .to(direct("get-purchaseinfo-by-id"))
                .to(sql("classpath:/sql/purchaseInfo/delete-purchaseInfo-by-id.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("{'message': 'Deleted'}", String.class)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("employee-exists"))
                .routeId("employee exists")
                .to(sql("classpath:/sql/employees/employee-exists.sql"))
                .choice()
                        .when(simple("${header.CamelSqlRowCount} < 1"))
                        .throwException(new NestedResourceNotFound())
                .end()
        ;
        from(direct("customer-exists"))
                .routeId("customer exists")
                .to(sql("classpath:/sql/customers/customer-exists.sql"))
                .choice()
                    .when(simple("${header.CamelSqlRowCount} < 1"))
                    .throwException(new NestedResourceNotFound())
                .end()
        ;

        from(direct("purchaseinfo-exists"))
                .routeId("purchaseinfo-exists")
                .to(sql("classpath:/sql/purchaseInfo/get-purchaseinfo-if-exists-by-id.sql"))
                .choice()
                    .when(simple("${header.CamelSqlRowCount} < 1"))
                    .throwException(new ResourceNotFound())
                .end()
        ;

        from(direct("product-exists"))
                .routeId("direct:product-exists")
                .loop(simple("${exchangeProperty.numberOfElements}", Integer.class))
                .transform(datasonnetEx("resource:classpath:/formats/purchaseinfos-products/insert-into-purchaseinfos-products.ds", Map.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JAVA))
                .to(sql("classpath:/sql/products/get-product-by-id.sql"))
                .choice()
                .when(simple("${header.CamelSqlRowCount} < 1"))
                .throwException(new NestedResourceNotFound())
                .end()
                .end()
        ;

        from(direct("delete-all-products"))
                .routeId("direct:delete-all-products")
                .to(sql("classpath:/sql/purchaseinfos-products/delete-all-products-from-purchaseinfos-products.sql"))
        ;

        from(direct("insert-into-purchaseinfos-products"))
                .routeId("direct:insert-into-purchaseinfos-products")
                .loop(simple("${exchangeProperty.numberOfElements}", Integer.class))
                .transform(datasonnetEx("resource:classpath:/formats/purchaseinfos-products/insert-into-purchaseinfos-products.ds", Map.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JAVA))
                .to(sql("classpath:/sql/purchaseinfos-products/insert-into-purchaseinfos-products.sql"))
                .end()
        ;

        from(direct("insert-into-purchaseinfos-products-for-create"))
                .routeId("direct:insert-into-purchaseinfos-products-for-create")
                .loop(simple("${exchangeProperty.numberOfElements}", Integer.class))
                .transform(datasonnetEx("resource:classpath:/formats/purchaseinfos-products/insert-into-purchaseinfos-products.ds", Map.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JAVA))
                .to(sql("classpath:/sql/purchaseinfos-products/insert-into-purchaseinfos-products-for-create.sql"))
                .end()
        ;
    }
}
