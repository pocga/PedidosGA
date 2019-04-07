package com.sophossolutions.pocga.test;

import com.sophossolutions.pocga.beans.BeanApiError;
import com.sophossolutions.pocga.beans.BeanCantidad;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.beans.BeanUsuario;
import com.sophossolutions.pocga.model.ServicioProductos;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

	private static final String ID_USUARIO = "1184ae2f-fbfb-4066-a5e3-727daf089439";
	private static final String ID_USUARIO_EX = "a1b60552-3cca-4002-a7f7-502aa1dac4b8";
	private static final String MODULO = "/carrito/";
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private ServicioProductos servicioProductos;
	
	@Before
	public void limpiarAntes() {
		// Vacía el carrito para el usuario de prueba
		vaciarCarrito();
	}

	@After
	public void limpiarDespues() {
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
		carritoEsperado.setUsuario(new BeanUsuario(ID_USUARIO));
		carritoEsperado.setProductos(new ArrayList<>());

		// Adiciona un producto
		adicionarProducto(producto1);
		carritoEsperado.getProductos().add(servicioProductos.fromBeanProducto(producto1));
		carritoEsperado.setTotales(new BeanTotales(10, 32998900));
		validarCarrito(carritoEsperado, "Adición producto 1");
		
		// Valida totales
		final BeanTotales totales1 = consultarTotales().getBody();
		Assert.assertEquals("Totales no coinciden para adición producto 1", carritoEsperado.getTotales(), totales1);
		
		// Adiciona otro producto
		adicionarProducto(producto2);
		adicionarProducto(producto3);
		carritoEsperado.getProductos().add(servicioProductos.fromBeanProducto(producto2));
		carritoEsperado.getProductos().add(servicioProductos.fromBeanProducto(producto3));
		carritoEsperado.setTotales(new BeanTotales(16, 53998150));
		validarCarrito(carritoEsperado, "Adición de producto 2 y 3");
		
		// Actualiza la cantidad del producto 1 a 8
		producto1.setCantidad(8);
		carritoEsperado.getProductos().set(0, servicioProductos.fromBeanProducto(producto1));
		actualizarProductoBodyFull(producto1);
		carritoEsperado.setTotales(new BeanTotales(14, 47398370));
		validarCarrito(carritoEsperado, "Actualización de cantidad producto 1 a 8");
		
		// Actualiza la cantidad del producto 2 a 1
		producto2.setCantidad(1);
		carritoEsperado.getProductos().set(1, servicioProductos.fromBeanProducto(producto2));
		carritoEsperado.setTotales(new BeanTotales(10, 32998810));
		actualizarProductoBodyCantidad(producto2);
		validarCarrito(carritoEsperado, "Actualización de cantidad producto 2");
		
		// Elimina el producto 2 del carrito
		eliminarProducto(producto2);
		carritoEsperado.getProductos().remove(1);
		carritoEsperado.setTotales(new BeanTotales(9, 29398920));
		validarCarrito(carritoEsperado, "Eliminar producto 2");
		
		// Pone en cero el producto 3
		producto3.setCantidad(0);
		actualizarProductoBodyFull(producto3);
		carritoEsperado.getProductos().remove(1);
		carritoEsperado.setTotales(new BeanTotales(8, 26399120));
		validarCarrito(carritoEsperado, "Actualizar a cero cantidad de producto 3");
		
		// Adiciona dos unidades del producto 1, ahora son 10
		producto1.setCantidad(2);
		adicionarProducto(producto1);
		carritoEsperado.setProductos(List.of(servicioProductos.fromBeanProducto(new BeanProducto(producto1.getIdProducto(), 10))));
		carritoEsperado.setTotales(new BeanTotales(10, 32998900));
		validarCarrito(carritoEsperado, "Adicionar producto 1, que ya existe en el carrito");
		
		// Elimina el producto 1, y se debe eliminar el carrito
		eliminarProducto(producto1);
		carritoEsperado.setUsuario(null);
		carritoEsperado.setProductos(null);
		carritoEsperado.setTotales(null);
		validarCarrito(carritoEsperado, "Vaciar el carrito");
	}
	
	@Test
	public void testExcepcionesCarrito() {
		// Consultar totales en carrito vacío
		final ResponseEntity<String> error1 = testRestTemplate.exchange(
			MODULO + "/" + ID_USUARIO_EX + "/productos/totales",
			HttpMethod.GET,
			HttpEntity.EMPTY,
			String.class
		);
		System.out.println(error1);
		Assert.assertEquals("Error buscando totales de carrito inexistente", HttpStatus.NOT_FOUND.value(), error1.getStatusCodeValue());

		// Adicionar un tipo de producto inválido
		final ResponseEntity<BeanApiError> error2 = testRestTemplate.postForEntity(MODULO + "/" + ID_USUARIO_EX + "/productos", (BeanProducto)null, BeanApiError.class);
		System.out.println(error2);
		Assert.assertEquals("Error creando producto sin cuerpo", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), error2.getStatusCodeValue());

		// Actualizar un producto para un usuario que no existe
		final ResponseEntity<BeanApiError> error3 = testRestTemplate.exchange(MODULO + "/" + UUID.randomUUID() + "/productos", HttpMethod.PUT, new HttpEntity<>(new BeanProducto()), BeanApiError.class);
		System.out.println(error3);
		Assert.assertEquals("Error actualizando carrito inexistente", HttpStatus.NOT_FOUND.value(), error3.getStatusCodeValue());
		
		// Crea un carrito
		testRestTemplate.postForEntity(MODULO + "/" + ID_USUARIO_EX + "/productos", new BeanProducto(5, 1), BeanApiError.class);
		
		// Producto inexistente
		final int idProductoNoExistente = new Random().nextInt();
		
		// Adicionar producto no registrado
		final ResponseEntity<BeanApiError> error4 = testRestTemplate.postForEntity(MODULO + "/" + ID_USUARIO_EX + "/productos", new BeanProducto(idProductoNoExistente, 1), BeanApiError.class);
		System.out.println(error4);
		Assert.assertEquals("Error creando producto inexistente", HttpStatus.NOT_FOUND.value(), error4.getStatusCodeValue());
		
		// Actualizar un producto inexistente en un carrito
		final ResponseEntity<BeanApiError> error5 = testRestTemplate.exchange(MODULO + "/" + ID_USUARIO_EX + "/productos", HttpMethod.PUT, new HttpEntity<>(new BeanProducto(idProductoNoExistente, 1)), BeanApiError.class);
		System.out.println(error5);
		Assert.assertEquals("Error actualizando producto inexistente", HttpStatus.NOT_FOUND.value(), error5.getStatusCodeValue());
		
		// Actualizar un producto inexistente en un carrito
		final ResponseEntity<BeanApiError> error6 = testRestTemplate.exchange(MODULO + "/" + ID_USUARIO_EX + "/productos/" + idProductoNoExistente, HttpMethod.PUT, new HttpEntity<>(new BeanCantidad(5)), BeanApiError.class);
		System.out.println(error6);
		Assert.assertEquals("Error actualizando producto inexistente", HttpStatus.NOT_FOUND.value(), error6.getStatusCodeValue());
		
		// Actualizar un producto que no está en un carrito
		final ResponseEntity<BeanApiError> error7 = testRestTemplate.exchange(MODULO + "/" + ID_USUARIO_EX + "/productos/" + 4, HttpMethod.PUT, new HttpEntity<>(new BeanCantidad(5)), BeanApiError.class);
		System.out.println(error7);
		Assert.assertEquals("Error actualizando producto inexistente", HttpStatus.UNPROCESSABLE_ENTITY.value(), error7.getStatusCodeValue());
		
		// Borrar un producto cualquiera de un carrito inexistente
		final ResponseEntity<BeanApiError> error8 = testRestTemplate.exchange(MODULO + "/" + UUID.randomUUID() + "/productos/" + idProductoNoExistente, HttpMethod.DELETE, null, BeanApiError.class);
		System.out.println(error8);
		Assert.assertEquals("Error eliminando producto de carrito inexistente", HttpStatus.NOT_FOUND.value(), error8.getStatusCodeValue());
		
		// Borrar un producto inexistente de un carrito
		final ResponseEntity<BeanApiError> error9 = testRestTemplate.exchange(MODULO + "/" + ID_USUARIO_EX + "/productos/" + idProductoNoExistente, HttpMethod.DELETE, null, BeanApiError.class);
		System.out.println(error9);
		Assert.assertEquals("Error eliminando producto inexistente", HttpStatus.NOT_FOUND.value(), error9.getStatusCodeValue());
		
		// Borrar carrito inexistente
		final ResponseEntity<BeanApiError> error10 = testRestTemplate.exchange(MODULO + "/" + UUID.randomUUID() + "/productos", HttpMethod.DELETE, null, BeanApiError.class);
		System.out.println(error10);
		Assert.assertEquals("Error eliminando carrito inexistente", HttpStatus.NOT_FOUND.value(), error10.getStatusCodeValue());
		
		// Adicionar una cantidad negativa a cualquier carrito
		final ResponseEntity<BeanApiError> error11 = testRestTemplate.postForEntity(MODULO + "/" + ID_USUARIO_EX + "/productos", new BeanProducto(idProductoNoExistente, -1), BeanApiError.class);
		System.out.println(error11);
		Assert.assertEquals("Error creando producto con cantidad negativa", HttpStatus.UNPROCESSABLE_ENTITY.value(), error11.getStatusCodeValue());
		
		// Adicionar una cantidad 0 a cualquier carrito
		final ResponseEntity<BeanApiError> error12 = testRestTemplate.postForEntity(MODULO + "/" + ID_USUARIO_EX + "/productos", new BeanProducto(idProductoNoExistente, 0), BeanApiError.class);
		System.out.println(error12);
		Assert.assertEquals("Error creando producto con cantidad en cero", HttpStatus.UNPROCESSABLE_ENTITY.value(), error12.getStatusCodeValue());

		// Actualizar cualquier producto con cantidad negativa
		final ResponseEntity<BeanApiError> error13 = testRestTemplate.exchange(MODULO + "/" + ID_USUARIO_EX + "/productos/", HttpMethod.PUT, new HttpEntity<>(new BeanProducto(5, -1)), BeanApiError.class);
		System.out.println(error13);
		Assert.assertEquals("Error actualizando producto con cantidad negativa", HttpStatus.UNPROCESSABLE_ENTITY.value(), error13.getStatusCodeValue());
		
		// Actualizar cualquier producto con más unidades que el inventario
		final ResponseEntity<BeanApiError> error14 = testRestTemplate.exchange(MODULO + "/" + ID_USUARIO_EX + "/productos/", HttpMethod.PUT, new HttpEntity<>(new BeanProducto(5, Integer.MAX_VALUE)), BeanApiError.class);
		System.out.println(error14);
		Assert.assertEquals("Error actualizando producto con cantidad superior al inventario", HttpStatus.UNPROCESSABLE_ENTITY.value(), error14.getStatusCodeValue());
		
		// Adicionar una cantidad superior al inventario
		final ResponseEntity<BeanApiError> error15 = testRestTemplate.postForEntity(MODULO + "/" + ID_USUARIO_EX + "/productos", new BeanProducto(1, Integer.MAX_VALUE), BeanApiError.class);
		System.out.println(error15);
		Assert.assertEquals("Error creando producto con cantidad superior al inventario", HttpStatus.UNPROCESSABLE_ENTITY.value(), error15.getStatusCodeValue());

		// Actualizar el carrito con producto que NO está en el carrito
		final ResponseEntity<BeanApiError> error16 = testRestTemplate.exchange(MODULO + "/" + ID_USUARIO_EX + "/productos/", HttpMethod.PUT, new HttpEntity<>(new BeanProducto(1, 1)), BeanApiError.class);
		System.out.println(error16);
		Assert.assertEquals("Error actualizando carrito con producto que no está", HttpStatus.NOT_FOUND.value(), error16.getStatusCodeValue());
		
		// Limpia el carrito
		testRestTemplate.delete("/carrito/" + ID_USUARIO_EX + "/productos");

		// Adicionar una cantidad superior al inventario
		final ResponseEntity<BeanApiError> error17 = testRestTemplate.postForEntity(MODULO + "/" + ID_USUARIO_EX + "/productos", new BeanProducto(1, Integer.MAX_VALUE), BeanApiError.class);
		System.out.println(error17);
		Assert.assertEquals("Error creando producto con cantidad superior al inventario", HttpStatus.UNPROCESSABLE_ENTITY.value(), error17.getStatusCodeValue());
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
		final HttpEntity<BeanCantidad> entity = new HttpEntity<>(new BeanCantidad(producto.getCantidad()), headers);

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
