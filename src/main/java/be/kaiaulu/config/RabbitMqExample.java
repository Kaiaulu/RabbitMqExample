package be.kaiaulu.config;

import com.rabbitmq.client.ConnectionFactory;
import de.codecentric.boot.admin.config.EnableAdminServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Optional;


@SpringBootApplication
@ComponentScan("be.kaiaulu.rabbitmq")
@EnableAdminServer
public class RabbitMqExample {

    @Value("${app.rabbitmq.host}")
    private String host;

    @Value("${app.rabbitmq.port}")
    private Integer port;

    @Value("${app.rabbitmq.username:#{null}}")
    private Optional<String> username;

    @Value("${app.rabbitmq.password:#{null}}")
    private Optional<String> password;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        username.ifPresent(connectionFactory::setUsername);
        password.ifPresent(connectionFactory::setPassword);
        return connectionFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitMqExample.class, args);
    }
}
