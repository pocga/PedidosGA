package com.sophossolutions.pocga.api;

import com.sophossolutions.pocga.api.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.api.exceptions.ErrorCreandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorListadoEntidadesVacio;
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
	public ResponseEntity<List<BeanPedido>> getPedidos() {
		final List<BeanPedido> listaPedidos = servicio.getPedidos();
		if(listaPedidos != null) {
			LOGGER.info("Consulta de todos los pedidos ({}) exitosa", listaPedidos.size());
			return new ResponseEntity<>(listaPedidos, HttpStatus.OK);
		} else {
			LOGGER.error("No hay pedidos registrados en el sistema -> {}", HttpStatus.NOT_FOUND);
			throw new ErrorListadoEntidadesVacio("No hay pedidos registrados en el sistema");
		}
	}

	@GetMapping("/{idPedido}")
	public ResponseEntity<BeanPedido> getPedido(@PathVariable UUID idPedido) {
		// Lo busca
		final BeanPedido pedido = servicio.getPedido(idPedido);
		if(pedido != null) {
			LOGGER.info("Consulta del pedido '{}' exitosa", idPedido);
			return new ResponseEntity<>(pedido, HttpStatus.OK);
		} else {
			LOGGER.error("No se encontró el pedido '{}'", idPedido);
			throw new ErrorEntidadNoEncontrada("No se encontró un pedido con el ID {" + idPedido + "}");
		}
	}

	@GetMapping("/usuarios/{idUsuario}")
	public ResponseEntity<List<BeanPedido>> getPedidos(@PathVariable String idUsuario) {
		// Lo busca
		final List<BeanPedido> listaPedidos = servicio.getPedidos(idUsuario);
		if(!listaPedidos.isEmpty()) {
			LOGGER.info("Consulta de todos los pedidos del usuario '{}' exitosa", idUsuario);
			return new ResponseEntity<>(listaPedidos, HttpStatus.OK);
		} else {
			LOGGER.error("No se encontraron pedidos para el usuario: {} -> {}", idUsuario, HttpStatus.NOT_FOUND);
			throw new ErrorListadoEntidadesVacio("No se encontraron pedidos para el usuario {" + idUsuario + "}");
		}
	}

	@PostMapping("")
	public ResponseEntity<BeanPedido> addPedido(@RequestBody BeanPedido pedido) {
		// Control
		if(Objects.isNull(pedido)) {
			LOGGER.error("No especificó el pedido a crear -> {}", HttpStatus.BAD_REQUEST);
			throw new IllegalArgumentException("No especificó el pedido a crear");
		}

		// Lo busca
		try {
			final BeanPedido pedidoCreado = servicio.crearPedido(pedido);
			LOGGER.info("Creación del pedido '{}' exitosa", pedidoCreado.getIdPedido());
			return new ResponseEntity<>(pedidoCreado, HttpStatus.CREATED);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("No se pudo crear el pedido, pues ya existe un pedido con el ID: {} -> {}", pedido.getIdPedido(), HttpStatus.UNPROCESSABLE_ENTITY);
			throw new ErrorCreandoEntidad(iae.getLocalizedMessage());
		}
	}
	
	@DeleteMapping("/{idPedido}")
	public ResponseEntity<HttpStatus> removePedido(@PathVariable UUID idPedido) {
		try {
			servicio.eliminarPedido(idPedido);
			LOGGER.info("Eliminación del pedido '{}' exitosa", idPedido);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException iae) {
			LOGGER.error("No se encontró un pedido con el ID: {} -> {}", idPedido, HttpStatus.NOT_FOUND);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
