package com.sophossolutions.pocga.api;

import com.sophossolutions.pocga.api.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.beans.BeanCrearPedido;
import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.cassandra.service.ServicioPedidos;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

	@Autowired
	private ServicioPedidos servicio;
	
	@GetMapping("")
	public List<BeanPedido> getPedidos() {
		return servicio.getPedidos();
	}

	@Cacheable(cacheNames = "pedidos", key = "#idPedido")
	@GetMapping("/{idPedido}")
	public BeanPedido getPedido(@PathVariable UUID idPedido) {
		return servicio.getPedido(idPedido);
	}

	@GetMapping("/usuarios/{idUsuario}")
	public List<BeanPedido> getPedidos(@PathVariable String idUsuario) {
		return servicio.getPedidos(idUsuario);
	}

	@PostMapping("")
	public ResponseEntity<BeanPedido> addPedido(@RequestBody BeanCrearPedido pedido) {
		final BeanPedido pedidoCreado = servicio.crearPedido(pedido);
		return new ResponseEntity<>(pedidoCreado, HttpStatus.CREATED);
	}

	@CacheEvict(cacheNames = "pedidos", key = "#idPedido")
	@DeleteMapping("/{idPedido}")
	public ResponseEntity<HttpStatus> removePedido(@PathVariable UUID idPedido) {
		try {
			servicio.eliminarPedido(idPedido);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (ErrorEntidadNoEncontrada eene) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/usuarios/{idUsuario}")
	public void removePedidosUsuario(@PathVariable String idUsuario) {
		servicio.eliminarPedidosUsuario(idUsuario);
	}

}
