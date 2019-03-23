package com.sophossolutions.pocga.test;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Configuración para pruebas
 * @author Ricardo José Ramírez Blauvelt
 */
@Configuration
public class RestTemplateConfiguration {
	
	@Bean
	public RestTemplateBuilder restTemplateBuilder() {
		return new RestTemplateBuilder().requestFactory(SimpleClientHttpRequestFactory.class);
	}

}
