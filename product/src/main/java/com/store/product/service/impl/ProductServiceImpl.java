package com.store.product.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.store.product.domain.Product;
import com.store.product.repository.ProductRepository;
import com.store.product.service.ProductService;

@Service
@Component
public class ProductServiceImpl extends GenericServiceImpl<Product, Long, ProductRepository> implements ProductService {

	@Autowired
	private ProductRepository rep;

	@Value("${rabbitmq.exchange.name}")
	private String exchange;

	@Value("${rabbitmq.routing.key}")
	private String routingKey;

	private final AmqpTemplate at;

	public ProductServiceImpl(ProductRepository rep, AmqpTemplate at) {
		super(rep);
		this.at = at;
	}

	public void testeMsg() {
		Product prod = new Product();
		prod.setId(1L);
		prod.setName("Iphone");
		prod.setDescription("Iphone Pro Max");
		prod.setCategory("D");
		prod.setPrice(7000.00F);
		at.convertAndSend(exchange, routingKey, prod);
		System.out.println("------CHEGUEI AQUI");
	}

	public void sendNotification(Product p) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			String json = mapper.writeValueAsString(p);

			at.convertAndSend(exchange, routingKey, json);
		} catch (JsonProcessingException e) {

		}
	}

}
