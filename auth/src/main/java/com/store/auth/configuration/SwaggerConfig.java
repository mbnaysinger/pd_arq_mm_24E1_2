package com.store.auth.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info=@Info(title="Título da minha doc", description = "Microsserviço de autenticação", version = "v1"))
public class SwaggerConfig {
}
