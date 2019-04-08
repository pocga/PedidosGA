package com.sophossolutions.pocga.test;

import com.datastax.driver.core.utils.UUIDs;
import com.sophossolutions.pocga.beans.BeanApiError;
import com.sophossolutions.pocga.beans.BeanCrearPedido;
import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.beans.BeanUsuario;
import com.sophossolutions.pocga.model.ServicioProductos;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

/**
 * Pruebas unitarias de la API de Pedidos
 * @author Ricardo José Ramírez Blauvelt
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(RestTemplateConfiguration.class)
public class TestApiPedidos {
	
	private static final UUID ID_PEDIDO = UUIDs.timeBased();
	private static final String ID_USUARIO = "75410a99-4011-4662-81e6-4514d3a48e77";
	private static final String ID_USUARIO_EX = "4fc8d378-e2aa-49ff-8f95-2465bcc68d25";
	private static final String MODULO = "/pedidos/";

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private ServicioProductos servicioProductos;
	
	@Before
	public void limpiarAntes() {
		servicioProductos.clearCacheProductos();
		eliminarPedidosUsuario();
	}	
	
	@After
	public void limpiarDespues() {
		servicioProductos.clearCacheProductos();
		eliminarPedidosUsuario();
	}	
	
	public TestApiPedidos() {
		
	}

	@Test
	public void testOperacionesPedidos() {
		// Simula el carrito
		final BeanProducto producto1 = new BeanProducto(1, 10);
		testRestTemplate.postForEntity("/carrito/" + ID_USUARIO + "/productos", producto1, BeanTotales.class);
		
		// Pasa por la función para dar cobertura
		servicioProductos.removeProducto(producto1.getIdProducto());

		// Crea un pedido
		final BeanPedido pedidoEsperado1 = new BeanPedido();
		pedidoEsperado1.setIdPedido(ID_PEDIDO);
		pedidoEsperado1.setUsuario(new BeanUsuario(ID_USUARIO));
		pedidoEsperado1.setProductos(List.of(servicioProductos.fromBeanProducto(producto1)));
		pedidoEsperado1.setFecha(LocalDateTime.now());
		pedidoEsperado1.setNombreDestinatario("Ricardo");
		pedidoEsperado1.setDireccionDestinatario("CL 48 20 34 OF 1009");
		pedidoEsperado1.setCiudadDestinatario("Medellín");
		pedidoEsperado1.setTelefonoDestinatario("+574 605 1010");
		crearPedido(pedidoEsperado1.toBeanCrearPedido());
		validarPedidos(List.of(pedidoEsperado1), "Adición del pedido 1");
		
		// Validar que el carrito ya no tenga productos
		final BeanTotales totales = testRestTemplate.getForObject("/carrito/" + ID_USUARIO + "/productos/totales", BeanTotales.class);
		Assert.assertEquals("El carrito del usuario no quedó vacío", 0, totales.getTotalCantidad());

		// Adiciona otro pedido
		final BeanPedido pedidoEsperado2 = new BeanPedido();
		pedidoEsperado2.setUsuario(pedidoEsperado1.getUsuario());
		pedidoEsperado2.setProductos(servicioProductos.fromMapProductos(Map.of(4, 1)));
		pedidoEsperado2.setNombreDestinatario("Ana");
		pedidoEsperado2.setDireccionDestinatario("CIR 3 71 59");
		pedidoEsperado2.setCiudadDestinatario("Medellín");
		pedidoEsperado2.setTelefonoDestinatario("+574 412 3456");
		final BeanPedido pedidoCreado2 = crearPedido(pedidoEsperado2.toBeanCrearPedido());
		pedidoEsperado2.setIdPedido(pedidoCreado2.getIdPedido());
		pedidoEsperado2.setFecha(pedidoCreado2.getFecha());
		validarPedidos(List.of(pedidoEsperado1, pedidoEsperado2), "Adición del pedido 2");

		// Elimina el primer pedido
		eliminarPedido();
		validarPedidos(List.of(pedidoEsperado2), "Eliminación del primer pedido");
		
		// Consulta el segundo pedido
		final BeanPedido pedidoReal2 = consultarPedido(pedidoEsperado2.getIdPedido());
		Assert.assertEquals("El pedido 2 creado no coincide", pedidoEsperado2, pedidoReal2);
		
		// Verifica el estado actual del carrito
		final List<BeanPedido> listaPedidosReal = consultarPedidos();
		Assert.assertEquals(
			"Debería quedar sólo un pedido para el usuario", 
			1, 
			listaPedidosReal.stream().filter(bp -> bp.getUsuario().getIdUsuario().equals(ID_USUARIO)).collect(Collectors.toList()).size()
		);
	}
	
	@Test
	public void testExcepcionesPedidos() {
		// Consultar pedido inexistente
		final ResponseEntity<String> error1 = testRestTemplate.exchange(
				MODULO + UUIDs.timeBased(),
				HttpMethod.GET,
				HttpEntity.EMPTY,
				String.class
		);
		System.out.println(error1);
		Assert.assertEquals("Error buscando pedido inexistente", HttpStatus.NOT_FOUND.value(), error1.getStatusCodeValue());
		
		// Crear pedido sin cuerpo
		final ResponseEntity<BeanApiError> error2 = testRestTemplate.postForEntity(MODULO, (BeanPedido)null, BeanApiError.class);
		System.out.println(error2);
		Assert.assertEquals("Error creando pedido sin cuerpo", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), error2.getStatusCodeValue());
		
		// Crea un pedido
		final BeanPedido pedido1 = new BeanPedido();
		pedido1.setIdPedido(UUIDs.timeBased());
		pedido1.setUsuario(new BeanUsuario(ID_USUARIO_EX));
		pedido1.setProductos(servicioProductos.fromMapProductos(Map.of(1, 1)));
		pedido1.setNombreDestinatario("Un nombre");
		pedido1.setDireccionDestinatario("Una dirección");
		pedido1.setCiudadDestinatario("Una ciudad");
		pedido1.setTelefonoDestinatario("Un teléfono");
		crearPedido(pedido1.toBeanCrearPedido());

		// Intenta crear otro con el mismo ID
		final BeanCrearPedido pedido2 = new BeanCrearPedido();
		pedido2.setIdPedido(pedido1.getIdPedido());
		pedido2.setIdUsuario(pedido1.getUsuario().getIdUsuario());
		pedido2.setProductos(List.of(new BeanProducto(2, 2)));
		pedido2.setNombreDestinatario("Un nombre");
		pedido2.setDireccionDestinatario("Una dirección");
		pedido2.setCiudadDestinatario("Una ciudad");
		pedido2.setTelefonoDestinatario("Un teléfono");
		final ResponseEntity<BeanApiError> error3 = testRestTemplate.postForEntity(MODULO, pedido2, BeanApiError.class);
		System.out.println(error3);
		Assert.assertEquals("Error duplicando pedido", HttpStatus.UNPROCESSABLE_ENTITY.value(), error3.getStatusCodeValue());
		
		// Intenta borrar un pedido inexistente
		final ResponseEntity<HttpStatus> error4 = testRestTemplate.exchange(MODULO + UUIDs.timeBased(), HttpMethod.DELETE, HttpEntity.EMPTY, HttpStatus.class);
		System.out.println(error4);
		Assert.assertEquals("Error borrando pedido inexistente", HttpStatus.NOT_FOUND.value(), error4.getStatusCodeValue());
		
		// Crear pedido con producto inexistente
		final BeanCrearPedido pedidoConProductoInexistente = new BeanCrearPedido();
		pedidoConProductoInexistente.setIdUsuario(ID_USUARIO_EX);
		pedidoConProductoInexistente.setProductos(List.of(new BeanProducto(new Random().nextInt(), 1)));
		pedidoConProductoInexistente.setNombreDestinatario("A");
		pedidoConProductoInexistente.setDireccionDestinatario("B");
		pedidoConProductoInexistente.setCiudadDestinatario("C");
		pedidoConProductoInexistente.setTelefonoDestinatario("D");
		final ResponseEntity<BeanApiError> error5 = testRestTemplate.postForEntity(MODULO, pedidoConProductoInexistente, BeanApiError.class);
		System.out.println(error5);
		Assert.assertEquals("Error creando pedido con producto inexistente", HttpStatus.NOT_FOUND.value(), error5.getStatusCodeValue());

		// Intenta crear pedido con cantidades por fuera del inventario
		final BeanCrearPedido pedido3 = new BeanCrearPedido.BeanCrearPedidoBuilder()
			.addIdUsuario(ID_USUARIO_EX)
			.addProductos(List.of(new BeanProducto(2, Integer.MAX_VALUE)))
			.addNombreDestinatario("A")
			.addDireccionDestinatario("B")
			.addCiudadDestinatario("C")
			.addTelefonoDestinatario("D")
			.build()
		;
		final ResponseEntity<BeanApiError> error6 = testRestTemplate.postForEntity(MODULO, pedido3, BeanApiError.class);
		System.out.println(error6);
		Assert.assertEquals("Error creando pedido por más unidades de las disponibles en el inventario", HttpStatus.UNPROCESSABLE_ENTITY.value(), error6.getStatusCodeValue());

		// Intenta crear pedido con un usuario inexistente
		final BeanCrearPedido pedido4 = new BeanCrearPedido.BeanCrearPedidoBuilder()
			.addIdUsuario(UUID.randomUUID().toString())
			.addProductos(List.of(new BeanProducto(2, 1)))
			.addNombreDestinatario("A")
			.addDireccionDestinatario("B")
			.addCiudadDestinatario("C")
			.addTelefonoDestinatario("D")
			.build()
		;
		final ResponseEntity<BeanApiError> error7 = testRestTemplate.postForEntity(MODULO, pedido4, BeanApiError.class);
		System.out.println(error7);
//		Assert.assertEquals("Error creando pedido con un usuario inexistente", HttpStatus.NOT_FOUND.value(), error7.getStatusCodeValue());

		// Intenta crear pedido sin usuario
		final BeanCrearPedido pedido5 = new BeanCrearPedido.BeanCrearPedidoBuilder()
			.addIdUsuario("")
			.addProductos(List.of(new BeanProducto(2, 1)))
			.addNombreDestinatario("A")
			.addDireccionDestinatario("B")
			.addCiudadDestinatario("C")
			.addTelefonoDestinatario("D")
			.build()
		;
		final ResponseEntity<BeanApiError> error8 = testRestTemplate.postForEntity(MODULO, pedido5, BeanApiError.class);
		System.out.println(error8);
		Assert.assertEquals("Error creando pedido sin especificar el usuario", HttpStatus.UNPROCESSABLE_ENTITY.value(), error8.getStatusCodeValue());

		// Intenta crear pedido sin productos
		final BeanCrearPedido pedido6 = new BeanCrearPedido.BeanCrearPedidoBuilder()
			.addIdUsuario(ID_USUARIO_EX)
			.addProductos(List.of())
			.addNombreDestinatario("A")
			.addDireccionDestinatario("B")
			.addCiudadDestinatario("C")
			.addTelefonoDestinatario("D")
			.build()
		;
		final ResponseEntity<BeanApiError> error9 = testRestTemplate.postForEntity(MODULO, pedido6, BeanApiError.class);
		System.out.println(error9);
		Assert.assertEquals("Error creando pedido sin especificar los productos", HttpStatus.UNPROCESSABLE_ENTITY.value(), error9.getStatusCodeValue());

		// Limpia
		testRestTemplate.delete(MODULO + pedido1.getIdPedido());
	}
	
	private void validarPedidos(List<BeanPedido> pedidosEsperados, String operacion) {
		// Consulta el carrito
		System.out.println(operacion);
		System.out.println("Pedidos esperado: " + pedidosEsperados);
		final List<BeanPedido> pedidosReal = consultarPedidosUsuario();
		System.out.println("Pedidos real:     " + pedidosReal);
		System.out.println("");

		// Compara los datos
		Assert.assertEquals(
			"Los pedidos no coinciden: " + operacion, 
			pedidosEsperados, 
			pedidosReal
		);
	}
	
	/**
	 * Consulta todos los pedidos
	 */
	private List<BeanPedido> consultarPedidos() {
		return consultaApiColeccion(
			MODULO, 
			new ParameterizedTypeReference<List<BeanPedido>>() {}
		);
	}
	
	/**
	 * Consulta un pedido
	 */
	private BeanPedido consultarPedido(UUID idPedido) {
		return consultaApi(MODULO + idPedido, BeanPedido.class);
	}
	
	/**
	 * Consulta los pedidos de un usuario
	 */
	private List<BeanPedido> consultarPedidosUsuario() {
		final List<BeanPedido> pedidos = consultaApiColeccion(
			MODULO + "usuarios/" + ID_USUARIO, 
			new ParameterizedTypeReference<List<BeanPedido>>() {}
		);
		return pedidos != null ? pedidos : List.of();
	}

	/**
	 * Procedimiento genérico para consultar una API que devuelve un objeto
	 */
	private <T> T consultaApi(String urlRecurso, Class<T> responseType) {
		try {
			final ResponseEntity<T> entity = testRestTemplate.exchange(
				urlRecurso,
				HttpMethod.GET,
				HttpEntity.EMPTY,
				responseType
			);
			return entity.getBody();

		} catch (RestClientException e) {
			System.out.println("Excepción: " + e.getClass().getSimpleName() + " - Mensaje: " + e.getLocalizedMessage());
			if (e instanceof HttpStatusCodeException) {
				final HttpStatusCodeException hsce = (HttpStatusCodeException) e;
				System.out.println(hsce.getStatusCode());
				System.out.println(hsce.getResponseBodyAsString());
			}
			return null;
		}
	}

	/**
	 * Procedimiento genérico para consultar una API que devuelve una colección de objetos
	 */
	private <T> T consultaApiColeccion(String urlRecurso, ParameterizedTypeReference<T> responseType) {
		try {
			final ResponseEntity<T> entity = testRestTemplate.exchange(
				urlRecurso,
				HttpMethod.GET,
				HttpEntity.EMPTY,
				responseType
			);
			return entity.getBody();

		} catch (RestClientException e) {
			System.out.println("Excepción: " + e.getClass().getSimpleName() + " - Mensaje: " + e.getLocalizedMessage());
			if (e instanceof HttpStatusCodeException) {
				final HttpStatusCodeException hsce = (HttpStatusCodeException) e;
				System.out.println(hsce.getStatusCode());
				System.out.println(hsce.getResponseBodyAsString());
			}
			return null;
		}
	}
	
	/**
	 * Crea un pedido
	 */
	private BeanPedido crearPedido(BeanCrearPedido pedidoCrear) {
		// Crea un pedido
		final ResponseEntity<BeanPedido> pedidoCreado = testRestTemplate.postForEntity(
			MODULO, 
			pedidoCrear, 
			BeanPedido.class
		);
		
		// Valida la respuesta
		Assert.assertEquals(
			"HttpStatus de creación errado", 
			HttpStatus.CREATED, 
			pedidoCreado.getStatusCode()
		);
		
		// Entrega el pedido creado
		return pedidoCreado.getBody();
	}

	/**
	 * Elimina un pedido
	 * @param idPedido 
	 */
	private void eliminarPedido() {
		final ResponseEntity<HttpStatus> pedidoBorrado = testRestTemplate.exchange(
			MODULO + ID_PEDIDO,
			HttpMethod.DELETE,
			HttpEntity.EMPTY,
			HttpStatus.class
		);
		if (pedidoBorrado.getStatusCode() != HttpStatus.NO_CONTENT) {
			Assert.fail("Falló el borrado del pedido {" + ID_PEDIDO + "}");
		}
	}

	/**
	 * Elimina los pedidos que tenga registrado el usuario
	 */
	private void eliminarPedidosUsuario() {
		testRestTemplate.delete(MODULO + "usuarios/" + ID_USUARIO);
	}

}
