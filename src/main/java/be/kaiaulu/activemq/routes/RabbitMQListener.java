package be.kaiaulu.activemq.routes;

import be.kaiaulu.activemq.model.SimpleObject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(RuntimeException.class)
                .routeId("simple-object-exception-route-B")
                .handled(true)
                .log(LoggingLevel.INFO, "Exception on processing")
                .marshal().json(JsonLibrary.Jackson, SimpleObject.class)
                .convertBodyTo(String.class)
                .setHeader(Exchange.EXCEPTION_CAUGHT, simple("${exception.stacktrace}"))
                .inOnly("rabbitmq:deadLetterChannel" +
                        "?queue=deadLetterQueue-A" +
                        "&exchangeType=topic" +
                        "&autoDelete=false" +
                        "&routingKey=simple-A")
                .end();

        from("rabbitmq:simple-Exchange" +
                    "?queue=task-queue-A" +
                    "&routingKey=simple-A" +
                    "&exchangeType=topic" +
                    "&autoDelete=false" +
                    "&deadLetterExchange=deadLetterChannel" +
                    "&deadLetterExchangeType=topic" +
                    "&deadLetterQueue=deadLetterQueue-A" +
                    "&deadLetterRoutingKey=simple-A")
                .routeId("simple-object-exception-route-A")
                .log(LoggingLevel.INFO, "Reading from RabbitMq")
                .unmarshal().json(JsonLibrary.Jackson, SimpleObject.class)
                .log(LoggingLevel.INFO, String.format("Received ${body.firstName} - ${body.lastName}"))
                .end();
    }
}
