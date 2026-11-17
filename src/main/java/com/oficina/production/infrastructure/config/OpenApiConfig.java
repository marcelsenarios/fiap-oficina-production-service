package com.oficina.production.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FIAP Oficina - Microsserviço de Execução e Produção (Production Service)")
                        .version("1.0.0")
                        .description("Microsserviço responsável pela gestão da esteira e fila de produção mecânica, apontamentos técnicos, persistência NoSQL (MongoDB) e compensação via Saga Pattern.")
                        .contact(new Contact()
                                .name("Carlos Marcel Sena Rios")
                                .email("marcel@fiap.com.br"))
                        .license(new License().name("Apache 2.0")));
    }
}
