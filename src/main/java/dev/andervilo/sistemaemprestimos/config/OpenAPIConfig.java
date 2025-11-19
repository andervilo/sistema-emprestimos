package dev.andervilo.sistemaemprestimos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Empréstimos Financeiros API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de empréstimos financeiros, incluindo gestão de clientes, " +
                                "solicitações de empréstimo, análise de crédito, pagamentos e garantias.")
                        .contact(new Contact()
                                .name("Andervilo")
                                .email("contato@andervilo.dev"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desenvolvimento")
                ));
    }
}
