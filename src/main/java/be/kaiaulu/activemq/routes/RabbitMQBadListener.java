package be.kaiaulu.activemq.routes;

import be.kaiaulu.activemq.model.SimpleObject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQBadListener extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(RuntimeException.class)
                .routeId("simple-object-exception-route-B")
                .handled(true)
                .log(LoggingLevel.INFO, "Exception on processing ${headers} ${exception}")
                .marshal().json(JsonLibrary.Jackson, SimpleObject.class)
                .convertBodyTo(String.class)
                .setHeader(Exchange.EXCEPTION_CAUGHT, simple("${exception.stacktrace}"))
                .inOnly("rabbitmq:deadLetterChannel" +
                        "?queue=deadLetterQueue-B" +
                        "&exchangeType=topic" +
                        "&autoDelete=false" +
                        "&routingKey=simple-B")
                .end();

        from("rabbitmq:simple-Exchange" +
                    "?queue=task-queue-B" +
                    "&routingKey=simple-B" +
                    "&exchangeType=topic" +
                    "&autoDelete=false" +
                    "&deadLetterExchange=deadLetterChannel" +
                    "&deadLetterExchangeType=topic" +
                    "&deadLetterQueue=deadLetterQueue-B" +
                    "&deadLetterRoutingKey=simple-B")
                .routeId("simple-object-exception-route-B")
                .log(LoggingLevel.INFO, "Reading from RabbitMq to throw exception ${headers}")
                .unmarshal().json(JsonLibrary.Jackson, SimpleObject.class)
                .log(LoggingLevel.INFO, String.format("Received ${body.firstName} - ${body.lastName}"))
                .throwException(new RuntimeException())
                .end();
    }
}
