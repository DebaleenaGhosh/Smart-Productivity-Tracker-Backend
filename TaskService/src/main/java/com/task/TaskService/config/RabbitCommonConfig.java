package com.task.TaskService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitCommonConfig
{
    // User events exchange
    public static final String USER_EVENTS_EXCHANGE = "user.events.exchange";
    // Task events exchange
    public static final String TASK_EVENTS_EXCHANGE = "task.events.exchange";
    // User events queue
    //public static final String USER_REGISTERED_QUEUE = "user.registered.queue.task-service";
    public static final String TASK_USER_EVENTS_QUEUE = "task.user.events.queue";

    /* Task events exchange bean */
    @Bean
    public TopicExchange taskEventsExchange()
    {
        return ExchangeBuilder.topicExchange(TASK_EVENTS_EXCHANGE).durable(true).build();
    }
    /* User events exchange bean */
    @Bean
    public TopicExchange userEventsExchange()
    {
        return ExchangeBuilder.topicExchange(USER_EVENTS_EXCHANGE).durable(true).build();
    }
//    @Bean
//    public Queue userRegisteredQueue() { return new Queue(USER_REGISTERED_QUEUE, true); }
    @Bean
    public Queue taskUserEventsQueue() { return QueueBuilder.durable(TASK_USER_EVENTS_QUEUE).build(); }

//    // Task service listens to user registration event
//    @Bean
//    public Binding bindingUserRegisteredQueue(Queue userRegisteredQueue, TopicExchange userEventsExchange)
//    {
//        return BindingBuilder.bind(userRegisteredQueue).to(userEventsExchange).with("user.registered");
//    }
    // Task service listens to all user.* events (e.g., user.created, user.updated)
    @Bean
    public Binding bindingTaskUserQueue(Queue taskUserEventsQueue, TopicExchange userEventsExchange)
    {
        return BindingBuilder.bind(taskUserEventsQueue).to(userEventsExchange).with("user.*");
    }

    // Message converter for JSON serialization (used by RabbitTemplate)
    @Bean
    public MessageConverter messageConverter()
    {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate bean for sending messages (injected into TaskEventPublisher)
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter)
    {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
