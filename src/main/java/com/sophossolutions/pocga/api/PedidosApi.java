package com.sophossolutions.pocga.api;

import com.sophossolutions.pocga.beans.BeanApiError;
import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.cassandra.service.ServicioPedidos;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para la API de PedidosApi
 * @author Ricardo José Ramírez Blauvelt
 */
@RestController
@RequestMapping("/pedidos")
public class PedidosApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(PedidosApi.class);
	
	@Autowired
	private ServicioPedidos servicio;
	
	@GetMapping("")
	public ResponseEntity<?> getPedidos() {
		final List<BeanPedido> listaPedidos = servicio.getPedidos();
		if(listaPedidos != null) {
			LOGGER.info("Consulta de todos los pedidos ({}) exitosa", listaPedidos.size());
			return new ResponseEntity<>(listaPedidos, HttpStatus.OK);
		} else {
			LOGGER.error("No hay pedidos registrados en el sistema -> {}", HttpStatus.NOT_FOUND);
			final BeanApiError error = new BeanApiError(
				HttpStatus.NOT_FOUND.toString(), 
				"No hay pedidos registrados en el sistema", 
				"", 
				"No hay ningún pedido para mostrar, pues no existen registros en el sistema"
			);
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/{stringIdPedido}")
	public ResponseEntity<?> getPedido(@PathVariable String stringIdPedido) {
		// Control
		UUID idPedido;
		try {
			idPedido = UUID.fromString(stringIdPedido);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("No especificó un ID de pedido válido: " + stringIdPedido + " -> {}", HttpStatus.BAD_REQUEST);
			final BeanApiError error = new BeanApiError(
				HttpStatus.BAD_REQUEST.toString(), 
				"No especificó un ID de pedido válido: " + stringIdPedido, 
				"", 
				"Se debe especificar un ID de pedido para la búsqueda, el cual es de tipo UUID. Verifique el recurso -> " + iae.getLocalizedMessage()
			);
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		
		// Lo busca
		final BeanPedido pedido = servicio.getPedido(idPedido);
		if(pedido != null) {
			LOGGER.info("Consulta del pedido '{}' exitosa", idPedido);
			return new ResponseEntity<>(pedido, HttpStatus.OK);
		} else {
			LOGGER.error("No se encontró un pedido con el ID: " + idPedido + " -> {}", HttpStatus.NOT_FOUND);
			final BeanApiError error = new BeanApiError(
				HttpStatus.NOT_FOUND.toString(), 
				"No se encontró un pedido con el ID: " + idPedido, 
				"", 
				"El ID de pedido especificado, no pudo ser encontrado en el sistema. Por favor verifique su consulta"
			);
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/usuarios/{idUsuario}")
	public ResponseEntity<?> getPedidos(@PathVariable String idUsuario) {
		// Control
		if(idUsuario == null) {
			LOGGER.error("No especificó un ID de usuario a buscar -> {}", HttpStatus.BAD_REQUEST);
			final BeanApiError error = new BeanApiError(
				HttpStatus.BAD_REQUEST.toString(), 
				"No especificó un ID de usuario a buscar", 
				"", 
				"Se debe especificar un ID de usuario para la búsqueda, el cual es de tipo String. Verifique el recurso"
			);
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}

		// Lo busca
		final List<BeanPedido> listaPedidos = servicio.getPedidos(idUsuario);
		if(listaPedidos != null) {
			LOGGER.info("Consulta de todos los pedidos del usuario '{}' exitosa", idUsuario);
			return new ResponseEntity<>(listaPedidos, HttpStatus.OK);
		} else {
			LOGGER.error("No se encontraron pedidos para el usuario: " + idUsuario + " -> {}", HttpStatus.NOT_FOUND);
			final BeanApiError error = new BeanApiError(
				HttpStatus.NOT_FOUND.toString(), 
				"No se encontraron pedidos para el usuario: " + idUsuario, 
				"", 
				"El ID de usuario especificado, no tiene pedidos registrados en el sistema. Por favor verifique su consulta"
			);
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("")
	public ResponseEntity<?> addPedido(@RequestBody BeanPedido pedido) {
		// Control
		if(Objects.isNull(pedido)) {
			LOGGER.error("No especificó el pedido a crear -> {}", HttpStatus.BAD_REQUEST);
			final BeanApiError error = new BeanApiError(
				HttpStatus.BAD_REQUEST.toString(), 
				"No especificó el pedido a crear", 
				"", 
				"Se debe especificar el recurso a crear y enviarlo en el body. Verifique el recurso"
			);
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}

		// Lo busca
		try {
			final BeanPedido pedidoCreado = servicio.crearPedido(pedido);
			if(pedidoCreado != null) {
				LOGGER.info("Creación del pedido '{}' exitosa", pedidoCreado.getIdPedido());
				return new ResponseEntity<>(pedidoCreado, HttpStatus.CREATED);
			} else {
				LOGGER.error("No se pudo crear el pedido: " + pedido + " -> {}", HttpStatus.BAD_REQUEST);
				final BeanApiError error = new BeanApiError(
					HttpStatus.BAD_REQUEST.toString(), 
					"No se pudo crear el pedido: " + pedido, 
					"", 
					"Ocurrió un error durante la creación del pedido y la operación no pudo finalizar"
				);
				return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
			}
		} catch (IllegalArgumentException iae) {
			LOGGER.error("No se pudo crear el pedido, pues ya existe un pedido con el ID: " + pedido.getIdPedido() + " -> {}", HttpStatus.UNPROCESSABLE_ENTITY);
			final BeanApiError error = new BeanApiError(
				HttpStatus.UNPROCESSABLE_ENTITY.toString(),
				"No se pudo crear el pedido, pues ya existe un pedido con el ID: " + pedido.getIdPedido(),
				"",
				"No se creó el pedido, pues ya registra en el sistema un pedido con el mismo ID -> " + iae.getLocalizedMessage()
			);
			return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
			
		}
	}
	
	@DeleteMapping("/{stringIdPedido}")
	public ResponseEntity<?> removePedido(@PathVariable String stringIdPedido) {
		// Control
		UUID idPedido;
		try {
			idPedido = UUID.fromString(stringIdPedido);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("No especificó un ID de pedido válido: " + stringIdPedido + " -> {}", HttpStatus.BAD_REQUEST);
			final BeanApiError error = new BeanApiError(
				HttpStatus.BAD_REQUEST.toString(), 
				"No especificó un ID de pedido válido: " + stringIdPedido, 
				"", 
				"Se debe especificar un ID de pedido válido para eliminar, el cual es de tipo UUID. Verifique el recurso -> " + iae.getLocalizedMessage()
			);
			return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
		}
		
		// Lo intenta eliminar
		try {
			servicio.eliminarPedido(idPedido);
			LOGGER.info("Eliminación del pedido '{}' exitosa", idPedido);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("No se encontró un pedido con el ID: " + idPedido + " -> {}", HttpStatus.NOT_FOUND);
			final BeanApiError error = new BeanApiError(
				HttpStatus.NOT_FOUND.toString(), 
				"No se eliminó un pedido con el ID: " + idPedido, 
				"", 
				"El ID de pedido especificado, no pudo ser encontrado en el sistema. Por favor verifique su consulta -> " + iae.getLocalizedMessage()
			);
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
		}
	}

}
