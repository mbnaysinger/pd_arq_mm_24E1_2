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
public class RabbitMQConfigProduct {
	@Value("${rabbitmq.queue.nameProd}")
	private String queue;

	@Value("${rabbitmq.exchange.nameProd}")
	private String exchange;

	@Value("${rabbitmq.routing.keyProd}")
	private String routingKey;

	@Bean
	public Queue queueProd() {
		return new Queue(queue);
	}

	@Bean
	public TopicExchange exchangeProd() {
		return new TopicExchange(exchange);
	}

	@Bean
	public Binding bindingProd() {
		return BindingBuilder.bind(queueProd()).to(exchangeProd()).with(routingKey);
	}
}
