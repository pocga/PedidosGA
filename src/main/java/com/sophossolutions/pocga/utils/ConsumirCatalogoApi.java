package com.sophossolutions.pocga.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Procedimiento que se encarga de traer los detalles de los productos desde la API de Catálogo
 * @author Ricardo José Ramírez Blauvelt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumirCatalogoApi {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumirCatalogoApi.class);

	/**
	 * Mock
	 */
	private static final ConcurrentMap<Integer, BeanDetallesProducto> PRODUCTOS = new ConcurrentHashMap<>();
	static {
		PRODUCTOS.put(1, new BeanDetallesProducto(1, "celulares", 10, 3299890, "Samsung Galaxy S10", "http://localhost/images/1/miniatura.jpg", "http://localhost/images/1/imagen.jpg"));
		PRODUCTOS.put(2, new BeanDetallesProducto(2, "celulares", 5, 3599890, "Samsung Galaxy S10+", "http://localhost/images/2/miniatura.jpg", "http://localhost/images/2/imagen.jpg"));
		PRODUCTOS.put(3, new BeanDetallesProducto(3, "celulares", 2, 2999800, "Google Pixel 3", "http://localhost/images/3/miniatura.jpg", "http://localhost/images/3/imagen.jpg"));
		PRODUCTOS.put(4, new BeanDetallesProducto(4, "televisores", 3, 1800000, "Sony Bravia XL100", "http://localhost/images/4/miniatura.jpg", "http://localhost/images/4/imagen.jpg"));
		PRODUCTOS.put(5, new BeanDetallesProducto(5, "televisores", 1, 1750000, "LG OLED Z40", "http://localhost/images/5/miniatura.jpg", "http://localhost/images/5/imagen.jpg"));
	}
	
	/**
	 * Consulta el producto en la API remota
	 * @param idProducto
	 * @return 
	 */
	public BeanDetallesProducto getProductoDesdeApi(int idProducto) {
		// Arma la URL de la API
		final UriComponents uriComponents = UriComponentsBuilder.newInstance()
			.scheme("https")
			.host("fvwzxk56cg.execute-api.us-east-1.amazonaws.com")
			.pathSegment("mock", "productos", "{idProducto}")
			.buildAndExpand(idProducto)
		;

		// Consulta los detalles del producto en la API
		final ResponseEntity<BeanDetallesProducto> response = new RestTemplateBuilder()
			.build()
			.getForEntity(
				uriComponents.toUri(), 
				BeanDetallesProducto.class
			);
		if(!response.getStatusCode().is2xxSuccessful()) {
			return null;
		}
		LOGGER.info("ConsumirCatalogoApi -> Producto encontrado en API remota: {}", response);
		
		// Control
		if(response.getBody().getIdProducto() == 0) {
			return null;
		}
		
		// Entrega el producto
		return response.getBody();
	}

}
