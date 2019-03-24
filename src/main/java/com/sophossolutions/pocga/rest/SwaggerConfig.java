package com.sophossolutions.pocga.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuración del generados automático del Swagger 2 de la API
 * @author Ricardo José Ramírez Blauvelt
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.sophossolutions.pocga.api"))
				.paths(PathSelectors.regex("/(carrito|pedidos).*"))
				.build()
				.apiInfo(apiInfo())
		;
	}

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html")
				.addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
	
	private springfox.documentation.service.ApiInfo apiInfo() {
		final springfox.documentation.service.Contact contacto = new springfox.documentation.service.Contact(
			"Sophos Solutions S.A.S.", 
			"http://www.sophossolutions.com", 
			"pocga@sophossolutions.com"
		);

		return new ApiInfoBuilder()
			.title("API de Pedidos")
			.description("API de Pedidos que soporta el carrito de compras y el registro de pedidos, para la PoC Digital de Grupo Aval, en Spring Boot y Cassandra")
			.version("1.0.0")
			.license("Apache License Version 2.0")
			.licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
			.contact(contacto)
			.build();
	}

}
