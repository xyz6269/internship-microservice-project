package com.example.orderservice.config;

import com.example.orderservice.entity.Order;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;


@Configuration
public class MQConfig {
        public static final String QUEUE = "ORDER_QUEUE";
    public static final String EXCHANGE = "ORDER_EXCHANGE";
    public static final String ROUTING_KEY = "ORDER_ROUTING";

    static final Order topicExchangeName = null;
    @Bean
    public Queue queue() {
        return new Queue(QUEUE);
    }
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory factory) {
        final RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(converter());
        return template;
    }

}
