package com.sophossolutions.pocga.service;

import com.sophossolutions.pocga.model.ServicioCarrito;
import com.datastax.driver.core.utils.UUIDs;
import com.sophossolutions.pocga.exceptions.ErrorCreandoEntidad;
import com.sophossolutions.pocga.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.exceptions.ErrorListadoEntidadesVacio;
import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanCrearPedido;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.model.ServicioPedidos;
import com.sophossolutions.pocga.entity.PedidosEntity;
import com.sophossolutions.pocga.repository.PedidosRepository;
import com.sophossolutions.pocga.model.ServicioProductos;
import com.sophossolutions.pocga.model.ServicioUsuarios;
import com.sophossolutions.pocga.utils.AES;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.apache.logging.log4j.util.Strings;
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
	
	@Autowired
	private ServicioUsuarios servicioUsuarios;
	
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
		// Validación
		final Map<String, String> datosEnvio = Map.of(
			"idUsuario", pedido.getIdUsuario(), 
			"nombreDestinatario", pedido.getNombreDestinatario(), 
			"direccionDestinatario", pedido.getDireccionDestinatario(), 
			"ciudadDestinatario", pedido.getCiudadDestinatario(), 
			"telefonoDestinatario", pedido.getTelefonoDestinatario()
		);
		datosEnvio.forEach((campo, valor) -> {
			if(Strings.isBlank(valor)) {
				final String error = "No especificó un valor para el campo '" + campo + "'. Valor ingresado: '" + valor + "'";
				LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, pedido.getIdPedido(), error);
				throw new ErrorCreandoEntidad(error);
			}
		});
		if(pedido.getProductos().isEmpty()) {
			final String error = "No especificó los productos que conforman el pedido'. Valor ingresado: '" + pedido.getProductos() + "'";
			LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, pedido.getIdPedido(), error);
			throw new ErrorCreandoEntidad(error);
		}

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
				final String error = "Producto agostado";
				LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, pedido.getIdPedido(), error);
				throw new ErrorCreandoEntidad(error);
			}
		});
		
		// Valida el usuario
		servicioUsuarios.getUserByIdUsuario(pedido.getIdUsuario());

		// Crea la entidad
		final PedidosEntity entity = new PedidosEntity();
		
		// Llena los campos
		try {
			entity.setIdPedido(pedido.getIdPedido() != null ? pedido.getIdPedido() : UUIDs.timeBased());
			entity.setIdUsuario(pedido.getIdUsuario());
			entity.setProductos(BeanProducto.toMap(pedido.getProductos()));
			entity.setNombreDestinatario(AES.cifrar(pedido.getNombreDestinatario()));
			entity.setDireccionDestinatario(AES.cifrar(pedido.getDireccionDestinatario()));
			entity.setCiudadDestinatario(AES.cifrar(pedido.getCiudadDestinatario()));
			entity.setTelefonoDestinatario(AES.cifrar(pedido.getTelefonoDestinatario()));
			entity.setFecha(pedido.getFecha() != null ? pedido.getFecha() : LocalDateTime.now());
		} catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			final String error = "No se pudo cifrar la información de envío del pedido";
			LOGGER.error("Error creando el pedido con ID '{}'. Error: {}", pedido.getIdPedido(), error);
			final ErrorCreandoEntidad eene = new ErrorCreandoEntidad(error);
			eene.addSuppressed(e);
			throw eene;
		}
		
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

	/**
	 * Procedimiento que calcula los totales del pedido
	 * @param pedido
	 * @return 
	 */
	private BeanTotales calcularTotalesPedidos(BeanPedido pedido) {
		// Calcula los totales
		final int cantidad = pedido.getProductos()
			.stream()
			.mapToInt(BeanCantidadProducto::getCantidad)
			.sum()
		;
		final int precio = pedido.getProductos()
			.stream()
			.mapToInt(bcp -> bcp.getCantidad() * bcp.getProducto().getPrecio())
			.sum()
		;
		
		// Entrega los totales
		return new BeanTotales(cantidad, precio);
	}

	@Override public BeanPedido fromEntity(PedidosEntity entity)  {
		// Crea el objeto
		final BeanPedido pedido = new BeanPedido();

		// Asigna los valores generales
		pedido.setIdPedido(entity.getIdPedido());
		pedido.setProductos(servicioProductos.fromMapProductos(entity.getProductos()));
		pedido.setFecha(entity.getFecha());

		// Trae el nombre del usuario de cognito a partir del ID
		try {
			pedido.setUsuario(servicioUsuarios.getUserByIdUsuario(entity.getIdUsuario()));
		} catch (Exception e) {
			final String error = "No se pudo obtener el nombre del usuario '" + entity.getIdUsuario() + "'";
			LOGGER.error("Error generando el pedido a partir de la entidad para el ID '{}'. Error: {}", entity.getIdPedido(), error);
			final ErrorEntidadNoEncontrada eene = new ErrorEntidadNoEncontrada(error);
			eene.addSuppressed(e);
			throw eene;
		}

		// Descifra la información
		try {
			pedido.setNombreDestinatario(AES.descifrar(entity.getNombreDestinatario()));
			pedido.setDireccionDestinatario(AES.descifrar(entity.getDireccionDestinatario()));
			pedido.setCiudadDestinatario(AES.descifrar(entity.getCiudadDestinatario()));
			pedido.setTelefonoDestinatario(AES.descifrar(entity.getTelefonoDestinatario()));
		} catch (Exception e) {
			final String error = "No se pudo descifrar la información de envío del pedido";
			LOGGER.error("Error generando el pedido a partir de la entidad para el ID '{}'. Error: {}", entity.getIdPedido(), error);
			final ErrorEntidadNoEncontrada eene = new ErrorEntidadNoEncontrada(error);
			eene.addSuppressed(e);
			throw eene;
		}
		
		// Calcula los totales
		pedido.setTotales(calcularTotalesPedidos(pedido));

		// Entrega la instancia
		return pedido;
	}

}
