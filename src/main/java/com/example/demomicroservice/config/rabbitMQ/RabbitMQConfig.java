package com.example.demomicroservice.config.rabbitMQ;

import com.the.common.constant.rabbitMQ.RabbitMQConstant;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setHost("10.1.43.160");
//        connectionFactory.setPort(5672);
//        connectionFactory.setUsername("ricky");
//        connectionFactory.setPassword("25092002");
//        return connectionFactory;
//    }

    @Bean
    public Queue queue() {
        return new Queue(RabbitMQConstant.Queue.QUEUE_AUTHOR, false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(RabbitMQConstant.Exchange.EXCHANGE_AUTHOR);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitMQConstant.Routing.ROUTING_AUTHOR);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "RabbitTemplate")
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.out.println("Failed to send message: " + cause);
                // Implement your retry or error handling logic here
            } else {
                System.out.println("Message sent successfully.");
            }
        });
        return rabbitTemplate;
    }
}