package be.kaiaulu.activemq.routes;

import be.kaiaulu.activemq.model.Producer;
import be.kaiaulu.activemq.model.SimpleObject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQProducer extends RouteBuilder {

    @Autowired
    private Producer<SimpleObject> simpleBean;

    @Override
    public void configure() throws Exception {

        from("timer:foobar?period=2000")
                .log(LoggingLevel.INFO, "Sending to RabbitMq")
                .bean(simpleBean)
                .marshal().json(JsonLibrary.Jackson, true)
                .to("rabbitmq:simple-Exchange" +
                        "?queue=task-queue-A" +
                        "&routingKey=simple-A" +
                        "&autoDelete=false" +
                        "&exchangeType=topic" +
                        "&deadLetterExchange=deadLetterChannel" +
                        "&deadLetterExchangeType=topic" +
                        "&deadLetterQueue=deadLetterQueue-A" +
                        "&deadLetterRoutingKey=simple-A")
                .to("rabbitmq:simple-Exchange" +
                        "?queue=task-queue-B" +
                        "&routingKey=simple-B" +
                        "&autoDelete=false" +
                        "&exchangeType=topic" +
                        "&deadLetterExchange=deadLetterChannel" +
                        "&deadLetterExchangeType=topic" +
                        "&deadLetterQueue=deadLetterQueue-B" +
                        "&deadLetterRoutingKey=simple-B")
                .end();
    }
}
