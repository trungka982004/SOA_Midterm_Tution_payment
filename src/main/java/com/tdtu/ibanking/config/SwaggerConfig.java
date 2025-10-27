package com.tdtu.ibanking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("iBanking System API")
                        .version("1.0.0")
                        .description("API documentation for iBanking System.")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("phong0818689834@gmail.com")));
    }
}
