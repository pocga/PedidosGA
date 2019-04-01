package com.sophossolutions.pocga.cassandra.service;

import com.sophossolutions.pocga.redis.service.ServicioCarrito;
import com.datastax.driver.core.utils.UUIDs;
import com.sophossolutions.pocga.api.exceptions.ErrorCreandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.api.exceptions.ErrorListadoEntidadesVacio;
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
	
	private static final String PLANTILLA_SISTEMA_SIN_PEDIDOS = "No hay pedidos registrados en el sistema";
	private static final String PLANTILLA_PEDIDO_NO_EXISTE = "No se encontró un pedido con el ID {%s}";
	private static final String PLANTILLA_USUARIO_NO_TIENE_PEDIDOS = "No se encontraron pedidos para el usuario {%s}";
	private static final String PLANTILLA_LOGGER_ERROR_ADICIONANDO = "Error adicionando pedido con ID '{}'. Error: {}";
	
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
			final String error = PLANTILLA_SISTEMA_SIN_PEDIDOS;
			LOGGER.error("Error consultado los pedidos. Error: {}", error);
			throw new ErrorListadoEntidadesVacio(error);
		}
		
		// Crea la lista (se trabaja con TreeSet para que lo ordene cronológicamente)
		final Set<BeanPedido> listaPedidos = new TreeSet<>();
		for(PedidosEntity pe : entidades) {
			// Carga el pedido
			listaPedidos.add(fromEntity(pe));
		}
		
		// Entrega todos los pedidos
		LOGGER.info("Consulta de todos los pedidos ({}) exitosa", listaPedidos.size());
		return new ArrayList<>(listaPedidos);
	}

	@Override public BeanPedido getPedido(UUID idPedido) {
		// Consulta la entidad
		final Optional<PedidosEntity> entity = repository.findById(idPedido);
		if(!entity.isPresent()) {
			final String error = String.format(PLANTILLA_PEDIDO_NO_EXISTE, idPedido);
			LOGGER.error("Error consultando pedido por ID. Error: {}", error);
			throw new ErrorEntidadNoEncontrada(error);
		}
		
		// Entrega el pedido
		LOGGER.info("Consulta del pedido '{}' exitosa", idPedido);
		return fromEntity(entity.get());
	}

	@Override public List<BeanPedido> getPedidos(String idUsuario) {
		// Filtra los pedidos por usuario
		final Iterable<PedidosEntity> entidades = repository.findAllByIdUsuario(idUsuario);
		if(!entidades.iterator().hasNext()) {
			final String error = String.format(PLANTILLA_USUARIO_NO_TIENE_PEDIDOS, idUsuario);
			LOGGER.error("Error consultando pedidos para el usuario '{}'. Error: {}", idUsuario, error);
			throw new ErrorListadoEntidadesVacio(error);
		}
		
		// Crea la lista
		final Set<BeanPedido> listaPedidos = new TreeSet<>();
		for(PedidosEntity pe : entidades) {
			// Carga el pedido
			listaPedidos.add(fromEntity(pe));
		}
		
		// Entrega todos los pedidos (se trabaja con TreeSet para que lo ordene cronológicamente)
		LOGGER.info("Consulta de todos los pedidos del usuario '{}' exitosa", idUsuario);
		return new ArrayList<>(listaPedidos);
	}

	@Override public BeanPedido crearPedido(BeanCrearPedido pedido) {
		// Ya existe
		if(pedido.getIdPedido() != null && repository.existsById(pedido.getIdPedido())) {
			final String error = "El ID de pedido {" + pedido.getIdPedido()  + "} ya existe y no se puede crear de nuevo";
			LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, pedido.getIdPedido(), error);
			throw new ErrorCreandoEntidad(error);
		}
		
		// Valida los productos
		pedido.getProductos().forEach(bcp -> {
			final BeanDetallesProducto bdp = servicioProductos.getProducto(bcp.getIdProducto());
			if(bdp == null) {
				final String error = "El producto {" + bcp.getIdProducto() + "} no existe en el catálogo";
				LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, pedido.getIdPedido(), error);
				throw new ErrorEntidadNoEncontrada(error);
			}
			if (bcp.getCantidad() > bdp.getCantidadDisponible()) {
				final String error = "Intentando crear un pedido por más unidades {" + bcp.getCantidad() + "} de las disponibles en el inventario {" + bdp.getCantidadDisponible() + "} para el producto {" + bcp.getIdProducto() + "}";
				LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, pedido.getIdPedido(), error);
				throw new ErrorCreandoEntidad(error);
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
			LOGGER.warn("Error eliminando el carrito del usuario '{}'. Error: {}", pedido.getIdUsuario(), re.getLocalizedMessage());
		}
		
		// Entrega el ID generado
		LOGGER.info("Creación del pedido '{}' exitosa", newEntity.getIdPedido());
		return fromEntity(newEntity);
	}

	@Override public void eliminarPedido(UUID idPedido) {
		if(repository.existsById(idPedido)) {
			repository.deleteById(idPedido);
			LOGGER.info("Eliminación del pedido '{}' exitosa", idPedido);
		} else {
			final String error = String.format(PLANTILLA_PEDIDO_NO_EXISTE, idPedido);
			LOGGER.warn("Error eliminando pedido '{}'. Error: {}", idPedido, error);
			throw new ErrorEntidadNoEncontrada(error);
		}
	}

	@Override public void eliminarPedidosUsuario(String idUsuario) {
		try {
			final List<BeanPedido> pedidosUsuario = getPedidos(idUsuario);
			pedidosUsuario.forEach(pedido -> 
				eliminarPedido(pedido.getIdPedido())
			);
		} catch (ErrorListadoEntidadesVacio elev) {
			LOGGER.warn("No hay pedidos para eliminar del usuario '{}'", idUsuario);
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
