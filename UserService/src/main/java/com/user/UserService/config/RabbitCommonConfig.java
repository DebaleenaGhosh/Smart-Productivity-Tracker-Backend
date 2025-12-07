package com.user.UserService.config;

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
    // Authentication service can publish to user service.
    // User service can publish to task service and vice versa.
    // Common User events exchange
    public static final String USER_EVENTS_EXCHANGE = "user.events.exchange";
    // Task events exchange
    public static final String TASK_EVENTS_EXCHANGE = "task.events.exchange";
    // User Registration event queue
    public static final String USER_REGISTERED_QUEUE = "user.registered.queue.user-service";
    // Task events queue
    public static final String USER_TASK_EVENTS_QUEUE = "user.task.events.queue";

    /* User events exchange bean */
    @Bean
    public TopicExchange userEventsExchange()
    {
        return ExchangeBuilder.topicExchange(USER_EVENTS_EXCHANGE).durable(true).build();
    }
    /* Task events exchange bean */
    @Bean
    public TopicExchange taskEventsExchange()
    {
        return ExchangeBuilder.topicExchange(TASK_EVENTS_EXCHANGE).durable(true).build();
    }

    /* User Registration */
    @Bean
    public Queue userRegisteredQueue(){ return new Queue(USER_REGISTERED_QUEUE, true); }

    // User service listens to user registration event
    @Bean
    public Binding bindingUserRegisteredQueue(Queue userRegisteredQueue, TopicExchange userEventsExchange)
    {
        return BindingBuilder.bind(userRegisteredQueue).to(userEventsExchange).with("user.registered");
    }

    /* User - Task Events Queue */
    @Bean
    public Queue userTaskEventsQueue()
    {
        return new Queue(USER_TASK_EVENTS_QUEUE, true);
    }
    // Binding for task.* events. User listens to all task events (task.created, task.updated, task.deleted via task.*)
    @Bean
    public Binding bindingUserTaskQueue(Queue userTaskEventsQueue, TopicExchange taskEventsExchange)
    {
        return BindingBuilder.bind(userTaskEventsQueue).to(taskEventsExchange).with("task.*");
    }

    @Bean
    public MessageConverter converter()
    {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory)
    {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}

