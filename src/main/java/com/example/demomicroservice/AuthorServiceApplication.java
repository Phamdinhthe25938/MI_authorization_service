package com.example.demomicroservice;

import com.example.demomicroservice.config.web.ApplicationContextProvider;
import com.the.common.constant.rabbitMQ.RabbitMQConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.support.Acknowledgment;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class AuthorServiceApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthorServiceApplication.class);

//    @Resource(name = "RabbitTemplate")
//    private AmqpTemplate rabbitTemplate;

    public static void main(String[] args) {
        SpringApplication.run(AuthorServiceApplication.class, args);

        AuthorServiceApplication authorServiceApplication = new AuthorServiceApplication();

        LOGGER.info("start ----------------------------- start");
//        authorServiceApplication.test();
    }

    public void test() {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        AmqpTemplate rabbitTemplate = (RabbitTemplate) applicationContext.getBean("RabbitTemplate");
        List<String> list = List.of("the","duc","nam");

        rabbitTemplate.convertSendAndReceive(RabbitMQConstant.Queue.QUEUE_AUTHOR, RabbitMQConstant.Routing.ROUTING_AUTHOR, list);
    }

    @RabbitListener(queues = {RabbitMQConstant.Queue.QUEUE_AUTHOR}, ackMode = "MANUAL")
    public String receiveMessageFromQueue(List<String> list, Acknowledgment ac ) {
        System.out.println("Received message from Queue 1: " + list.get(0));
        ac.acknowledge();
        return list.get(0) + "ok ban nha";
    }

//    @RabbitListener(queues = RabbitMQConstant.Queue.QUEUE_AUTHOR)
//    public String receiveMessageFromQueue2(String message) throws InterruptedException {
//        System.out.println("Received message from Queue 2: " + message);
//
//        Thread.sleep(3000);
//        return message + " ok nha toi la queue 2";
//    }
}
