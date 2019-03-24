package com.sophossolutions.pocga.cassandra.service;

import com.datastax.driver.core.utils.UUIDs;
import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.cassandra.entity.PedidosEntity;
import com.sophossolutions.pocga.cassandra.repository.PedidosRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de pedidos
 * @author Ricardo José Ramírez Blauvelt
 */
@Service
public class ServicioPedidosImpl implements ServicioPedidos {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicioPedidosImpl.class);
	
	@Autowired
	private PedidosRepository repository;
	
	@Autowired
	private ServicioCarritoImpl servicioCarrito;

	@Override public List<BeanPedido> getPedidos() {
		// Consulta todos los pedidos
		final Iterable<PedidosEntity> entidades = repository.findAll();
		if(!entidades.iterator().hasNext()) {
			return List.of();
		}
		
		// Crea la lista
		final Set<BeanPedido> listaPedidos = new TreeSet<>();
		for(PedidosEntity pe : entidades) {
			// Carga el pedido
			listaPedidos.add(BeanPedido.fromEntity(pe));
		}
		
		// Entrega todos los pedidos (se trabaja con TreeSet para que lo ordene cronológicamente)
		return new ArrayList<>(listaPedidos);
	}

	@Override public BeanPedido getPedido(UUID idPedido) {
		// Consulta la entidad
		final Optional<PedidosEntity> entity = repository.findById(idPedido);
		if(!entity.isPresent()) {
			return null;
		}
		
		// Entrega el pedido
		return BeanPedido.fromEntity(entity.get());
	}

	@Override public List<BeanPedido> getPedidos(String idUsuario) {
		// Filtra los pedidos por usuario
		final Iterable<PedidosEntity> entidades = repository.findAllByIdUsuario(idUsuario);
		if(!entidades.iterator().hasNext()) {
			return List.of();
		}
		
		// Crea la lista
		final Set<BeanPedido> listaPedidos = new TreeSet<>();
		for(PedidosEntity pe : entidades) {
			// Carga el pedido
			listaPedidos.add(BeanPedido.fromEntity(pe));
		}
		
		// Entrega todos los pedidos (se trabaja con TreeSet para que lo ordene cronológicamente)
		return new ArrayList<>(listaPedidos);
	}

	@Override public BeanPedido crearPedido(BeanPedido pedido) {
		// Control
		if(pedido == null) {
			return null;
		}
		
		// Ya existe
		if(pedido.getIdPedido() != null && repository.existsById(pedido.getIdPedido())) {
			throw new IllegalArgumentException("El ID de pedido {" + pedido.getIdPedido()  + "} ya existe y no se puede crear de nuevo");
		}

		// Crea la entidad
		final PedidosEntity entity = new PedidosEntity();
		
		// Llena los campos
		entity.setIdPedido(pedido.getIdPedido() != null ? pedido.getIdPedido() : UUIDs.timeBased());
		entity.setIdUsuario(pedido.getIdUsuario());
		entity.setProductos(BeanCantidadProducto.toMapProductos(pedido.getProductos()));
		entity.setNombreDestinatario(pedido.getNombreDestinatario());
		entity.setDireccionDestinatario(pedido.getDireccionDestinatario());
		entity.setCiudadDestinatario(pedido.getCiudadDestinatario());
		entity.setTelefonoDestinatario(pedido.getTelefonoDestinatario());
		entity.setFecha(pedido.getFecha() != null ? pedido.getFecha() : LocalDateTime.now());
		
		// Registra la entidad
		final PedidosEntity newEntity = repository.save(entity);
		
		// Elimina el carrito del usuario
		servicioCarrito.eliminarCarrito(pedido.getIdUsuario());
		
		// Entrega el ID generado
		return BeanPedido.fromEntity(newEntity);
	}

	@Override public void eliminarPedido(UUID idPedido) {
		if(repository.existsById(idPedido)) {
			repository.deleteById(idPedido);
		} else {
			throw new IllegalArgumentException("El ID de pedido {" + idPedido  + "} no existe y por tanto, no fue necesario eliminarlo");
		}
	}

}
