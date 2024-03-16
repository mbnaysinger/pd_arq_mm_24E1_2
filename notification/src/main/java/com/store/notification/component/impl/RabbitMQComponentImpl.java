package com.store.notification.component.impl;

import com.store.notification.component.RabbitMQComponent;
import com.store.notification.service.impl.EmailServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RabbitMQComponentImpl implements RabbitMQComponent {
	@Value("${rabbitmq.queue.name}")
	private String queue;

	@Autowired
	private EmailServiceImpl emailServiceImpl;

	private final WebClient webClient;

	private final WebClient webClientProduct = WebClient.create("http://localhost:8087/api");

	public RabbitMQComponentImpl(WebClient webClient) {
		this.webClient = webClient;
	}

	@RabbitListener(queues = "order_notification")
	public void handleMessage(String message) {
		System.out.println("obj: " + message);
		Map<String, Object> obj = emailServiceImpl.convertToObject(message);

		int user_id = (int) obj.get("user_id");
		List<Map<String, Object>> orderItems = (List<Map<String, Object>>) obj.get("orderItems");

		// TODO: pega os produtos do carrinho e add no array para buscar o nome
		List<Integer> productIds = new ArrayList<>();
		for (Map<String, Object> orderItem : orderItems) {
			int productId = (int) orderItem.get("product_id");
			productIds.add(productId);
		}

		// TODO: pegar o produto do microservice de Product via http
		String productData = retrieveProduct(productIds.get(0));

		String response = this.webClient.get().uri("/user/" + String.valueOf(user_id)).retrieve()
				.bodyToMono(String.class).block();

		Map<String, Object> user = emailServiceImpl.convertToObject(response);
		Map<String, Object> product = emailServiceImpl.convertToObject(productData);

		String content = emailServiceImpl.constructOrderContent((String) product.get("name"),
				(String) user.get("username"));

		emailServiceImpl.sendEmail(content, (String) user.get("email"), "Notificação XPTO");
	}

	@RabbitListener(queues = "payment_notification")
	public void handlePaymentMessages(String message) {
		Map<String, Object> obj = emailServiceImpl.convertToObject(message);
		Integer paymentId = (Integer) obj.get("id");

		// TODO: pegar o nome do user no pagamento (criar no microsserviço de payment).

		String content = emailServiceImpl.constructPaymentContent("Maike Naysinger Borges", paymentId);

		emailServiceImpl.sendEmail(content, "moara.britz@gmail.com", "Notificação de Pagamento");
	}

	@RabbitListener(queues = "product_notification")
	public void handleProductMessages(String message) {
		System.out.println("------messagem: " + message);
		Map<String, Object> obj = emailServiceImpl.convertToObject(message);
		Integer prodId = (Integer) obj.get("id");
		String prod = (String) obj.get("name");

		// TODO: pegar o nome do user no pagamento (criar no microsserviço de payment).

		String content = emailServiceImpl.constructProductContent(prod);

		emailServiceImpl.sendEmail(content, "maikenborges@gmail.com", "Produto Processado");
	}

	private String retrieveProduct(int product_id) {
		String response = webClientProduct.get().uri("/product/" + String.valueOf(product_id)).retrieve()
				.bodyToMono(String.class).block();
		return response;
	}
}
