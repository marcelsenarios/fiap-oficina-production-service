package com.oficina.production.infrastructure.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.production:production.events.exchange}")
    private String productionExchangeName;

    @Value("${rabbitmq.exchange.os:os.events.exchange}")
    private String osExchangeName;

    @Value("${rabbitmq.queue.production-os-events:production.os.events.queue}")
    private String productionOsQueueName;

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public TopicExchange productionExchange() {
        return new TopicExchange(productionExchangeName, true, false);
    }

    @Bean
    public TopicExchange osExchange() {
        return new TopicExchange(osExchangeName, true, false);
    }

    @Bean
    public Queue productionOsQueue() {
        return QueueBuilder.durable(productionOsQueueName).build();
    }

    @Bean
    public Binding bindingProductionToOs(Queue productionOsQueue, TopicExchange osExchange) {
        return BindingBuilder.bind(productionOsQueue).to(osExchange).with("os.#");
    }
}
