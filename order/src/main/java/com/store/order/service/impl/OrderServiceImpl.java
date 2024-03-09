package com.store.order.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.order.domain.Order;
import com.store.order.domain.OrderItem;
import com.store.order.repository.OrderItemRepository;
import com.store.order.repository.OrderRepository;
import com.store.order.service.OrderService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Component
public class OrderServiceImpl extends GenericServiceImpl<Order, Long, OrderRepository> implements OrderService {
	private final WebClient webClient;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Value("${rabbitmq.exchange.name}")
	private String exchange;

	@Value("${rabbitmq.routing.key}")
	private String routingKey;

	private final AmqpTemplate rabbitTemplate;

	private final WebClient webClientPayment = WebClient.create("http://localhost:8089/api");

	public OrderServiceImpl(OrderRepository repository, WebClient webClient, AmqpTemplate rabbitTemplate) {
		super(repository);
		this.webClient = webClient;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void save(Order order) {
		// Busca dados do usuário que realizou a compra e notifica o fechamento do pedido
		this.webClient.get().uri("/user/" + String.valueOf(order.getUser_id())).accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.OK)) {
						Order ord = repository.save(order);
						for (OrderItem item : ord.getOrderItems()) {
							OrderItem orderItem = new OrderItem();

							orderItem.setOrder(ord);
							orderItem.setProduct_id(item.getProduct_id());

							orderItemRepository.save(orderItem);
						}

						this.sendNotification(ord);
						return response.toEntity(String.class);
					} else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
						System.out.println("User not found");
						return response.toEntity(String.class);
					} else {
						return response.createError();
					}
				}).block();

		//notifica pagamento via http
		String jsonBody = "{\"paymentSystem\": \"Mastercard\", \"installments\": 1, \"paymentValue\": 4500.00}";

		this.webClientPayment.post().uri("/payment").contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(jsonBody)).retrieve().bodyToMono(String.class)
				.subscribe(response -> System.out.println("Resposta: " + response));
	}

	public void sendNotification(Order order) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			String json = mapper.writeValueAsString(order);

			rabbitTemplate.convertAndSend(exchange, routingKey, json);
		} catch (JsonProcessingException e) {
		}
	}
}
