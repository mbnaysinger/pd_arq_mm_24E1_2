package com.store.order.configuration;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;

public class StripeConfig {
    public static void stripePay(String[] args) {
        // Configura a chave da API do Stripe
        Stripe.apiKey = "sk_test_51Oumo408bwORrTQsAVAypWQH3HNYhd3yW9Y8T7ttcY0xWUXVK8zIVYCP4JXjjb7aHRhFbB3iinC6EQdJcGyiYIO600ZIWAkeoz";

        // Cria os parâmetros para o pagamento
        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(2000L) //R$20,00 (utiliza menor valor da moeda)
                .setCurrency("brl")
                .setSource("tok_mastercard")
                .setDescription("Exemplo de pagamento")
                .build();

        try {
            // Tenta processar o pagamento
            Charge charge = Charge.create(params);
            System.out.println(charge);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }
}
