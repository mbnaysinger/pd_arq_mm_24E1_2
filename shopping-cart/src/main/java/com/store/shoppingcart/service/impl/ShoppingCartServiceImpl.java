package com.store.shoppingcart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.store.shoppingcart.domain.ShoppingCart;
import com.store.shoppingcart.domain.ShoppingCartItem;
import com.store.shoppingcart.repository.ShoppingCartRepositoryItemRepository;
import com.store.shoppingcart.repository.ShoppingCartRepository;
import com.store.shoppingcart.service.ShopppingCartService;
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
public class ShoppingCartServiceImpl extends GenericServiceImpl<ShoppingCart, Long, ShoppingCartRepository> implements ShopppingCartService {
	private final WebClient webClient;

	@Autowired
	private ShoppingCartRepositoryItemRepository shoppingCartRepositoryItemRepository;

	@Value("${rabbitmq.exchange.name}")
	private String exchange;

	@Value("${rabbitmq.routing.key}")
	private String routingKey;

	private final AmqpTemplate rabbitTemplate;

	private final WebClient webClientPayment = WebClient.create("http://localhost:8089/api");

	public ShoppingCartServiceImpl(ShoppingCartRepository repository, WebClient webClient, AmqpTemplate rabbitTemplate) {
		super(repository);
		this.webClient = webClient;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void save(ShoppingCart sc) {
		// Busca dados do usuário que realizou a compra e notifica o fechamento do pedido
		this.webClient.get().uri("/user/" + String.valueOf(sc.getUser_id())).accept(MediaType.APPLICATION_JSON)
				.exchangeToMono(response -> {
					if (response.statusCode().equals(HttpStatus.OK)) {
						ShoppingCart ord = repository.save(sc);
						for (ShoppingCartItem item : ord.getShoppingCartItems()) {
							ShoppingCartItem shoppingCartItem = new ShoppingCartItem();

							shoppingCartItem.setSc(ord);
							shoppingCartItem.setProduct_id(item.getProduct_id());

							shoppingCartRepositoryItemRepository.save(shoppingCartItem);
						}

						this.sendNotification(sc);
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

	public void sendNotification(ShoppingCart sc) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			String json = mapper.writeValueAsString(sc);

			rabbitTemplate.convertAndSend(exchange, routingKey, json);
		} catch (JsonProcessingException e) {
		}
	}
}
