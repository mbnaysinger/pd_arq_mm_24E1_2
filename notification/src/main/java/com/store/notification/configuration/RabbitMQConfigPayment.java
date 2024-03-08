package com.store.notification.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfigPayment {
	@Value("${rabbitmq.queue.namePayment}")
	private String queue;

	@Value("${rabbitmq.exchange.namePayment}")
	private String exchange;

	@Value("${rabbitmq.routing.keyPayment}")
	private String routingKey;

	@Bean
	public Queue queuePayment() {
		return new Queue(queue);
	}

	@Bean
	public TopicExchange exchangePayment() {
		return new TopicExchange(exchange);
	}

	@Bean
	public Binding bindingPayment() {
		return BindingBuilder.bind(queuePayment()).to(exchangePayment()).with(routingKey);
	}
}
