package com.example.mq.demo;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Controller
@EnableAutoConfiguration
@SpringBootApplication
public class DemoApplication {

    public static final String EXCHANGE_NAME = "rabbitmq_exchange";
    public static final String QUEUE_NAME = "rabbitmq_queue";
    public static final String ROUTING_KEY = "hello";


    @Autowired
    private AmqpTemplate rabbitTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 发送数据
     */
    @RequestMapping(value = "/sendMQ")
    @ResponseBody
    public String send() {
        String context = "hello " + new Date();
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, context);
        return "send success";
    }


    /**
     * 接收数据
     */
    @RabbitListener(containerFactory = "rabbitListenerContainerFactory",
            bindings = @QueueBinding(exchange = @Exchange(value = EXCHANGE_NAME,
                    durable = "true", autoDelete = "false"),
                    value = @Queue(value = QUEUE_NAME,
                            durable = "true", autoDelete = "false", exclusive = "false"),
                    key = ROUTING_KEY))
    public void receive(Object body) {
        try {
            Message message1 = (Message) body;

            String message = new String(message1.getBody(), "UTF-8");

            System.out.printf("receiver：" + message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
