package com.store.payment.service.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.payment.domain.Payment;
import com.store.payment.repository.PaymentRepository;
import com.store.payment.service.PaymentService;

@Service
@Component
public class PaymentServiceImpl extends GenericServiceImpl<Payment, Long, PaymentRepository> implements PaymentService {

	@Value("${rabbitmq.exchange.name}")
	private String exchange;

	@Value("${rabbitmq.routing.key}")
	private String routingKey;

	private final AmqpTemplate rabbitTemplate;

	public PaymentServiceImpl(PaymentRepository repository, AmqpTemplate rabbitTemplate) {
		super(repository);

		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void save(Payment payment) {
		repository.save(payment);
		this.sendNotification(payment);
	}
	
	
	public void sendNotification(Payment payment) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			String json = mapper.writeValueAsString(payment);

			rabbitTemplate.convertAndSend(exchange, routingKey, json);
		} catch (JsonProcessingException e) {
		}
	}
}
