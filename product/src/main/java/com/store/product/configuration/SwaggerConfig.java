package com.store.product.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info=@Info(title="Products", description = "Products Microservice", version = "v1"))
public class SwaggerConfig {
}
