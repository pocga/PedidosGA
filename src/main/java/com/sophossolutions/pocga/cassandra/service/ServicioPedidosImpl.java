package com.sophossolutions.pocga.cassandra.service;

import com.datastax.driver.core.utils.UUIDs;
import com.sophossolutions.pocga.api.exceptions.ErrorCreandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.beans.BeanCrearPedido;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.cassandra.entity.PedidosEntity;
import com.sophossolutions.pocga.cassandra.repository.PedidosRepository;
import com.sophossolutions.pocga.redis.service.ServicioProductos;
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
	private ServicioCarrito servicioCarrito;

	@Autowired
	private ServicioProductos servicioProductos;

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
			listaPedidos.add(fromEntity(pe));
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
		return fromEntity(entity.get());
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
			listaPedidos.add(fromEntity(pe));
		}
		
		// Entrega todos los pedidos (se trabaja con TreeSet para que lo ordene cronológicamente)
		return new ArrayList<>(listaPedidos);
	}

	@Override public BeanPedido crearPedido(BeanCrearPedido pedido) {
		// Ya existe
		if(pedido.getIdPedido() != null && repository.existsById(pedido.getIdPedido())) {
			throw new ErrorCreandoEntidad("El ID de pedido {" + pedido.getIdPedido()  + "} ya existe y no se puede crear de nuevo");
		}
		
		// Valida los productos
		pedido.getProductos().forEach(bcp -> {
			final BeanDetallesProducto bdp = servicioProductos.getProducto(bcp.getIdProducto());
			if(bdp == null) {
				throw new ErrorEntidadNoEncontrada("El producto {" + bcp.getIdProducto() + "} no existe en el catálogo");
			}
			if (bcp.getCantidad() > bdp.getCantidadDisponible()) {
				throw new ErrorCreandoEntidad("Intentando crear un pedido por más unidades {" + bcp.getCantidad() + "} de las disponibles en el inventario {" + bdp.getCantidadDisponible() + "} para el producto {" + bcp.getIdProducto() + "}");
			}
		});

		// Crea la entidad
		final PedidosEntity entity = new PedidosEntity();
		
		// Llena los campos
		entity.setIdPedido(pedido.getIdPedido() != null ? pedido.getIdPedido() : UUIDs.timeBased());
		entity.setIdUsuario(pedido.getIdUsuario());
		entity.setProductos(BeanProducto.toMap(pedido.getProductos()));
		entity.setNombreDestinatario(pedido.getNombreDestinatario());
		entity.setDireccionDestinatario(pedido.getDireccionDestinatario());
		entity.setCiudadDestinatario(pedido.getCiudadDestinatario());
		entity.setTelefonoDestinatario(pedido.getTelefonoDestinatario());
		entity.setFecha(pedido.getFecha() != null ? pedido.getFecha() : LocalDateTime.now());
		
		// Registra la entidad
		final PedidosEntity newEntity = repository.save(entity);
		
		// Elimina el carrito del usuario
		try {
			servicioCarrito.eliminarCarrito(pedido.getIdUsuario());
		} catch (RuntimeException re) {
			LOGGER.warn("Error eliminando el carrito: {}", re.getLocalizedMessage());
		}
		
		// Entrega el ID generado
		return fromEntity(newEntity);
	}

	@Override public void eliminarPedido(UUID idPedido) {
		if(repository.existsById(idPedido)) {
			repository.deleteById(idPedido);
		} else {
			throw new IllegalArgumentException("El ID de pedido {" + idPedido  + "} no existe y por tanto, no fue necesario eliminarlo");
		}
	}

	@Override public BeanPedido fromEntity(PedidosEntity entity) {
		// Crea la entidad
		final BeanPedido pedido = new BeanPedido();
		pedido.setIdPedido(entity.getIdPedido());
		pedido.setIdUsuario(entity.getIdUsuario());
		pedido.setProductos(servicioProductos.fromMapProductos(entity.getProductos()));
		pedido.setNombreDestinatario(entity.getNombreDestinatario());
		pedido.setDireccionDestinatario(entity.getDireccionDestinatario());
		pedido.setCiudadDestinatario(entity.getCiudadDestinatario());
		pedido.setTelefonoDestinatario(entity.getTelefonoDestinatario());
		pedido.setFecha(entity.getFecha());

		// La entrega
		return pedido;
	}

}
