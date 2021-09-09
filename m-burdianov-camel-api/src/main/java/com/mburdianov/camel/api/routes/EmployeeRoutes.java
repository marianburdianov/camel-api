package com.mburdianov.camel.api.routes;

import com.datasonnet.document.MediaTypes;
import com.mburdianov.camel.api.BaseRestRouteBuilder;
import com.mburdianov.camel.api.exceptions.ResourceNotFound;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.Media;

@Component
public class EmployeeRoutes extends BaseRestRouteBuilder {

    @Override
    public void configure() throws Exception {
        super.configure();

        from(direct("get-employees"))
                .routeId("direct:get-employees")
                .to(sql("classpath:/sql/employees/get-all-employees.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("resource:classpath:/formats/employees/get-all-employees.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("post-employee"))
                .routeId("direct:post-employee")
                .setProperty("firstName", datasonnetEx("payload.firstName", String.class))
                .setProperty("lastName", datasonnetEx("payload.lastName", String.class))
                .setProperty("gender", datasonnetEx("payload.gender", String.class))
                .setProperty("birthDate", datasonnetEx("payload.birthDate", String.class))
                .setProperty("hireDate", datasonnetEx("payload.hireDate", String.class))
                .setProperty("role", datasonnetEx("payload.role", String.class))
                .setProperty("salary", datasonnetEx("payload.salary", String.class))
                .transform(datasonnetEx("resource:classpath:/formats/employees/create-employee.ds", String.class))
                .to(sql("classpath:/sql/employees/create-employee.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .transform(datasonnetEx("{'message': 'Created'}", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("get-employee-by-id"))
                .routeId("direct:get-employee-by-id")
                .to(sql("classpath:/sql/employees/get-employee-by-id.sql"))
                .choice()
                .when(simple("${header.CamelSqlRowCount} < 1"))
                .throwException(new ResourceNotFound())
                .end()
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("resource:classpath:/formats/employees/get-employee-by-id.ds", String.class)
                        .bodyMediaType(MediaTypes.APPLICATION_JAVA)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("update-employee-by-id"))
                .routeId("direct:update-employee-by-id")
                .setProperty("firstName", datasonnetEx("payload.firstName", String.class))
                .setProperty("lastName", datasonnetEx("payload.lastName", String.class))
                .setProperty("gender", datasonnetEx("payload.gender", String.class))
                .setProperty("birthDate", datasonnetEx("payload.birthDate", String.class))
                .setProperty("hireDate", datasonnetEx("payload.hireDate", String.class))
                .setProperty("role", datasonnetEx("payload.role", String.class))
                .setProperty("salary", datasonnetEx("payload.salary", String.class))
                .transform(datasonnetEx("resource:classpath:/formats/employees/update-employee-by-id.ds", String.class))
                .to(direct("get-employee-by-id"))
                .to(sql("classpath:/sql/employees/update-employee-by-id.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("{'message': 'Updated'}", String.class)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
        from(direct("delete-employee-by-id"))
                .routeId("direct:delete-employee-by-id")
                .to(direct("get-employee-by-id"))
                .to(sql("classpath:/sql/employees/delete-employee-by-id.sql"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200))
                .transform(datasonnetEx("{'message': 'Deleted'}", String.class)
                        .outputMediaType(MediaTypes.APPLICATION_JSON))
        ;
    }
}
