package com.sophossolutions.pocga.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * Procedimiento que se encarga de traer los detalles de los productos desde la API de Catálogo
 * @author Ricardo José Ramírez Blauvelt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumirCatalogoApi {
	
	private static final String URL_CATALOGO_REST_API = "http://localhost:8080/catalogo/productos/";

	@Autowired
	private RestTemplate restTemplate;

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
	
	public static BeanDetallesProducto getProducto(int idProducto) {
		// Consulta los detalles del producto en la API
//		return restTemplate.getForObject(URL_CATALOGO_REST_API + idProducto, BeanDetallesProducto.class);

		// Mock
		return PRODUCTOS.get(idProducto);
	}

}
