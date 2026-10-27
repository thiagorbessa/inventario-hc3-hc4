package com.inventario.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Inventário de TI - HC3 & HC4")
                        .description("Documentação interativa das rotas REST para gestão unificada de equipamentos de TI, monitores, infraestrutura de rede e usuários.")
                        .version("v2.0.0")
                        .contact(new Contact()
                                .name("Equipe de TI HC3/HC4")
                                .email("suporte.ti@hospital.gov.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}