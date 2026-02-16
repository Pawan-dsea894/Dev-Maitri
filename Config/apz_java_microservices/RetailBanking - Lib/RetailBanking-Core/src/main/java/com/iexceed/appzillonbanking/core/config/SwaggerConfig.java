package com.iexceed.appzillonbanking.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.models.OpenAPI;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Appzillon Banking Services", description = "Appzillon Banking Services", contact = @Contact(name = "i-exceed technology solutions private limited", url = "https://i-exceed.com", email = "hr@i-exceed.com"), license = @License(name = "Proprietary", url = "https://i-exceed.com/contact-us/"), termsOfService = "https://i-exceed.com/contact-us/", version = "1.0.0"))
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI();
	}
}