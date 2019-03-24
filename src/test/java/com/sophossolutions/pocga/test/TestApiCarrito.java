package com.sophossolutions.pocga.test;

import com.sophossolutions.pocga.beans.BeanCantidad;
import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.rest.ConsumirCatalogoApi;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Pruebas unitarias de la API de Carrito
 * @author Ricardo José Ramírez Blauvelt
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(RestTemplateConfiguration.class)
public class TestApiCarrito {

	private static final String ID_USUARIO = "ricardo";
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Before
	public void limpiar() {
		// Vacía el carrito para el usuario de prueba
		vaciarCarrito();
	}

	@Test
	public void testOperacionesCarrito() {
		// Productos de prueba
		final BeanProducto producto1 = new BeanProducto(1, 10);
		final BeanProducto producto2 = new BeanProducto(2, 5);
		final BeanProducto producto3 = new BeanProducto(3, 1);
		
		// Carrito esperado
		final BeanDetallesCarrito carritoEsperado = new BeanDetallesCarrito();
		carritoEsperado.setIdUsuario(ID_USUARIO);
		carritoEsperado.setProductos(new ArrayList<>());

		// Adiciona un producto
		adicionarProducto(producto1);
		carritoEsperado.getProductos().add(BeanCantidadProducto.fromBeanProducto(producto1));
		carritoEsperado.setTotales(new BeanTotales(10, 32998900));
		validarCarrito(carritoEsperado, "Adición producto 1");
		
		// Adiciona otro producto
		adicionarProducto(producto2);
		adicionarProducto(producto3);
		carritoEsperado.getProductos().add(BeanCantidadProducto.fromBeanProducto(producto2));
		carritoEsperado.getProductos().add(BeanCantidadProducto.fromBeanProducto(producto3));
		carritoEsperado.setTotales(new BeanTotales(16, 53998150));
		validarCarrito(carritoEsperado, "Adición de producto 2 y 3");
		
		// Actualiza la cantidad del producto 1 a 8
		producto1.setCantidadPedida(8);
		carritoEsperado.getProductos().set(0, BeanCantidadProducto.fromBeanProducto(producto1));
		actualizarProductoBodyFull(producto1);
		carritoEsperado.setTotales(new BeanTotales(14, 47398370));
		validarCarrito(carritoEsperado, "Actualización de cantidad producto 1 a 8");
		
		// Actualiza la cantidad del producto 2 a 1
		producto2.setCantidadPedida(1);
		carritoEsperado.getProductos().set(1, BeanCantidadProducto.fromBeanProducto(producto2));
		carritoEsperado.setTotales(new BeanTotales(10, 32998810));
		actualizarProductoBodyCantidad(producto2);
		validarCarrito(carritoEsperado, "Actualización de cantidad producto 2");
		
		// Elimina el producto 2 del carrito
		eliminarProducto(producto2);
		carritoEsperado.getProductos().remove(1);
		carritoEsperado.setTotales(new BeanTotales(9, 29398920));
		validarCarrito(carritoEsperado, "Eliminar producto 2");
		
		// Pone en cero el producto 3
		producto3.setCantidadPedida(0);
		actualizarProductoBodyFull(producto3);
		carritoEsperado.getProductos().remove(1);
		carritoEsperado.setTotales(new BeanTotales(8, 26399120));
		validarCarrito(carritoEsperado, "Actualizar a cero cantidad de producto 3");
		
		// Vacía el carrito
		vaciarCarrito();
		carritoEsperado.getProductos().clear();
		carritoEsperado.setTotales(new BeanTotales(0, 0));
		validarCarrito(null, "Vaciar el carrito");
	}
	
	private void validarCarrito(BeanDetallesCarrito carritoEsperado, String operacion) {
		// Consulta el carrito
		System.out.println(operacion);
		System.out.println("Carrito esperado: " + carritoEsperado);
		final ResponseEntity<BeanDetallesCarrito> carritoReal = consultarCarrito();
		System.out.println("Carrito real:     " + carritoReal);
		System.out.println("");

		// Valida el carrito
		Assert.assertEquals(
			"El carrito no coincide: " + operacion,
			carritoEsperado,
			carritoReal.getBody()
		);
	}

	private ResponseEntity<BeanDetallesCarrito> consultarCarrito() {
		// Consulta el carrito del usuario
		final ResponseEntity<BeanDetallesCarrito> response = testRestTemplate.getForEntity(
			"/carrito/" + ID_USUARIO + "/productos", 
			BeanDetallesCarrito.class
		);
		return response;
	}
	
	private ResponseEntity<BeanTotales> consultarTotales() {
		final ResponseEntity<BeanTotales> response = testRestTemplate.getForEntity(
			"/carrito/" + ID_USUARIO + "/productos/totales", 
			BeanTotales.class
		);
		return response;
	}
	
	private ResponseEntity<BeanTotales> adicionarProducto(BeanProducto producto) {
		// Adiciona un producto al carrito
		return testRestTemplate.postForEntity(
			"/carrito/" + ID_USUARIO + "/productos",
			producto,
			BeanTotales.class
		);
	}
	
	private ResponseEntity<BeanTotales> actualizarProductoBodyFull(BeanProducto producto) {
		// Configura el mensaje
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		final HttpEntity<BeanProducto> entity = new HttpEntity<>(producto, headers);
		
		// Llama la API
		return testRestTemplate.exchange(
			"/carrito/" + ID_USUARIO + "/productos", 
			HttpMethod.PUT, 
			entity, 
			BeanTotales.class
		);
	}
	
	private ResponseEntity<BeanTotales> actualizarProductoBodyCantidad(BeanProducto producto) {
		// Configura el mensaje
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		final HttpEntity<BeanCantidad> entity = new HttpEntity<>(new BeanCantidad(producto.getCantidadPedida()), headers);

		return testRestTemplate.exchange(
			"/carrito/" + ID_USUARIO + "/productos/" + producto.getIdProducto(),
			HttpMethod.PUT,
			entity,
			BeanTotales.class
		);
	}
	
	private ResponseEntity<BeanTotales> eliminarProducto(BeanProducto producto) {
		return testRestTemplate.exchange(
			"/carrito/" + ID_USUARIO + "/productos/" + producto.getIdProducto(),
			HttpMethod.DELETE,
			null,
			BeanTotales.class
		);
	}
	
	private ResponseEntity<BeanTotales> vaciarCarrito() {
		return testRestTemplate.exchange(
			"/carrito/" + ID_USUARIO + "/productos",
			HttpMethod.DELETE,
			null,
			BeanTotales.class
		);
	}

}
