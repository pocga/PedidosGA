package com.sophossolutions.pocga.test;

import com.datastax.driver.core.utils.UUIDs;
import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanPedido;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
	private static final String ID_USUARIO = UUID.randomUUID().toString();
	private static final String MODULO = "/pedidos/";

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Before
	public void limpiarAntes() {
		eliminarPedidosUsuario();
	}	
	
	@After
	public void limpiarDespues() {
		eliminarPedidosUsuario();
	}	
	
	public TestApiPedidos() {
		
	}

	@Test
	public void testOperacionesPedidos() {
		// Crea un pedido
		final BeanPedido pedidoEsperado1 = new BeanPedido();
		pedidoEsperado1.setIdPedido(ID_PEDIDO);
		pedidoEsperado1.setIdUsuario(ID_USUARIO);
		pedidoEsperado1.setProductos(BeanCantidadProducto.fromMapProductos(Map.of(1, 3, 2, 1, 3, 1)));
		pedidoEsperado1.setFecha(LocalDateTime.now());
		pedidoEsperado1.setNombreDestinatario("Ricardo");
		pedidoEsperado1.setDireccionDestinatario("CL 48 20 34 OF 1009");
		pedidoEsperado1.setCiudadDestinatario("Medellín");
		pedidoEsperado1.setTelefonoDestinatario("+574 605 1010");
		crearPedido(pedidoEsperado1);
		validarPedidos(List.of(pedidoEsperado1), "Adición del pedido 1");

		// Adiciona otro pedido
		final BeanPedido pedidoEsperado2 = new BeanPedido();
		pedidoEsperado2.setIdUsuario(ID_USUARIO);
		pedidoEsperado2.setProductos(BeanCantidadProducto.fromMapProductos(Map.of(4, 1)));
		pedidoEsperado2.setNombreDestinatario("Ana");
		pedidoEsperado2.setDireccionDestinatario("CIR 3 71 59");
		pedidoEsperado2.setCiudadDestinatario("Medellín");
		pedidoEsperado2.setTelefonoDestinatario("+574 412 3456");
		final BeanPedido pedidoCreado2 = crearPedido(pedidoEsperado2);
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
			listaPedidosReal.stream().filter(bp -> bp.getIdUsuario().equals(ID_USUARIO)).collect(Collectors.toList()).size()
		);
	}
	
	private void validarPedidos(List<BeanPedido> pedidosEsperados, String operacion) {
		// Consulta el carrito
		System.out.println(operacion);
		System.out.println("Pedidos esperado: " + pedidosEsperados);
		final List<BeanPedido> pedidosReal = consultarPedidosUsuario();
		System.out.println("Pedidos real:     " + pedidosReal);
		System.out.println("");
		
		// Valida el tamaño del carrito
		Assert.assertEquals(
			"La cantidad de pedidos no coincide: " + operacion,
			pedidosEsperados.size(),
			pedidosReal.size()
		);

		// Valida el contenido
		for(int i = 0 ; i < pedidosEsperados.size() ; i++) {
			Assert.assertEquals(
				"Los IDs de los pedidos no coinciden: " + operacion, 
				pedidosEsperados.get(i).getIdPedido(), 
				pedidosReal.get(i).getIdPedido()
			);
			Assert.assertEquals(
				"Los IDs de los usuarios no coinciden: " + operacion, 
				pedidosEsperados.get(i).getIdUsuario(), 
				pedidosReal.get(i).getIdUsuario()
			);
			Assert.assertEquals(
				"Los productos del pedido no coinciden: " + operacion, 
				pedidosEsperados.get(i).getProductos(), 
				pedidosReal.get(i).getProductos()
			);
		}
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
	private BeanPedido crearPedido(BeanPedido pedidoCrear) {
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
		// Si no hay pedidos, no hay que borrar nada
		final List<BeanPedido> pedidosUsuario = consultarPedidosUsuario();
		if(pedidosUsuario.isEmpty()) {
			return;
		}
		
		// Borra los pedidos del usuario
		pedidosUsuario.forEach(pedido -> {
			// Borra el pedido
			final ResponseEntity<HttpStatus> pedidoBorrado = testRestTemplate.exchange(
				MODULO + pedido.getIdPedido(),
				HttpMethod.DELETE,
				HttpEntity.EMPTY,
				HttpStatus.class
			);
			if (pedidoBorrado.getStatusCode() != HttpStatus.NO_CONTENT) {
				Assert.fail("Falló el borrado del pedido {" + pedido.getIdPedido() + "}");
			}
		});
	}

}
