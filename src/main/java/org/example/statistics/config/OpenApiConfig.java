package org.example.statistics.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {
	@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI()
				.info(new Info()
						.title("Statistics Server API")
						.version("1")
						.description("API for match statistics across servers"))
				.addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
				.components(
						new Components()
								.addSecuritySchemes(
										"BearerAuth",
										new SecurityScheme()
												.name("Authorization")
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")
								)
				);
	}
}
