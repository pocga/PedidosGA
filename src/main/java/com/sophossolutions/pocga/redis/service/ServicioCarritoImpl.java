package com.sophossolutions.pocga.redis.service;

import com.sophossolutions.pocga.api.exceptions.ErrorActualizandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorCreandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.api.exceptions.ErrorListadoEntidadesVacio;
import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.redis.entity.CarritoEntity;
import com.sophossolutions.pocga.redis.repository.CarritoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para el carrito de compras
 * @author Ricardo José Ramírez Blauvelt
 */
@Service
public class ServicioCarritoImpl implements ServicioCarrito {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicioCarritoImpl.class);

	private static final String PLANTILLA_PRODUCTO_NO_EXISTE = "El producto %s no está registrado en el sistema";
	private static final String PLANTILLA_CANTIDAD_SUPERA_INVENTARIO = "Producto agotado";
	private static final String PLANTILLA_CARRITO_NO_EXISTE = "No existe el carrito de compras del usuario %s";
	private static final String PLANTILLA_PRODUCTO_NO_EN_CARRITO = "No se encontró el producto %s en el carrito del usuario %s";
	private static final String PLANTILLA_LOGGER_ERROR_ADICIONANDO = "Error adicionando producto al carrito de '{}'. Error: {}";
	private static final String PLANTILLA_LOGGER_ERROR_ACTUALIZANDO = "Error actualizando productos del carrito de '{}'. Error: {}";

	@Autowired
	private CarritoRepository repository;
	
	@Autowired
	private ServicioProductos servicioProductos;
	
	@Autowired
	private ServicioUsuarios servicioUsuarios;
	
	@Override public BeanDetallesCarrito getCarrito(String idUsuario) {
		// Consulta
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		if(!optional.isPresent()) {
			final String error = String.format(PLANTILLA_CARRITO_NO_EXISTE, idUsuario);
			LOGGER.error("Error consultando carrito de '{}'. Error: {}", idUsuario, error);
			throw new ErrorListadoEntidadesVacio(error);
		}

		// Trae el detalle
		final CarritoEntity entity = optional.get();
		
		// Construye los detalles del carrito
		final List<BeanCantidadProducto> listaProductos = new ArrayList<>();
		entity.getProductos().forEach((producto, cantidad) -> {
			final BeanCantidadProducto bcp = new BeanCantidadProducto();
			bcp.setCantidad(cantidad);
			final BeanDetallesProducto detallesProducto = servicioProductos.getProducto(producto);
			if(detallesProducto == null) {
				final String error = String.format(PLANTILLA_PRODUCTO_NO_EXISTE, producto);
				LOGGER.error("Error consultando carrito de '{}'. Error: {}", idUsuario, error);
				throw new ErrorEntidadNoEncontrada(error);
			}
			bcp.setProducto(detallesProducto);
			listaProductos.add(bcp);
		});
		
		// Arma y entrega los detalles
		LOGGER.info("Consulta del carrito del usuario '{}' exitosa", idUsuario);
		final BeanDetallesCarrito detalles = new BeanDetallesCarrito();
		detalles.setUsuario(servicioUsuarios.getUserByIdUsuario(idUsuario));
		detalles.setProductos(listaProductos);
		detalles.setTotales(getTotalesCarrito(listaProductos));
		return detalles;
	}

	@Override public BeanTotales getTotalesCarrito(String idUsuario) {
		// Trae todos los productos en el carrito
		final BeanDetallesCarrito detalles = getCarrito(idUsuario);

		// Calcula los totales
		LOGGER.info("Consulta de los totales del carrito del usuario '{}' exitosa", idUsuario);
		return getTotalesCarrito(detalles.getProductos());
	}
	
	/**
	 * Procedimiento que hace el cálculo interno de los totales del carrito, a partir de los 
	 * productos
	 * @param productosEnCarrito
	 * @return 
	 */
	private BeanTotales getTotalesCarrito(List<BeanCantidadProducto> productosEnCarrito) {
		// Calcula el total de unidades
		final int totalCantidad = productosEnCarrito.stream().mapToInt(BeanCantidadProducto::getCantidad).sum();

		// Calcula el precio total
		final int totalPrecio = productosEnCarrito.stream().mapToInt(bcp -> bcp.getCantidad() * bcp.getProducto().getPrecio()).sum();

		// Crea el objeto
		return new BeanTotales(totalCantidad, totalPrecio);
	}

	@Override public BeanTotales adicionarProducto(String idUsuario, BeanProducto producto) {
		// Valida la cantidad
		if(producto.getCantidad() < 1) {
			final String error = "No se puede adicionar un producto con cantidad inferior a 1";
			LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, idUsuario, error);
			throw new ErrorCreandoEntidad(error);
		}

		// Valida la existencia del producto en el catálogo
		final BeanDetallesProducto detallesProducto = servicioProductos.getProducto(producto.getIdProducto());
		if(detallesProducto == null) {
			final String error = String.format(PLANTILLA_PRODUCTO_NO_EXISTE, producto.getIdProducto());
			LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, idUsuario, error);
			throw new ErrorEntidadNoEncontrada(error);
		}
		
		// Consulta el carrito
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		
		// Determina si se crea el carrito o se adiciona un producto
		if(!optional.isPresent()) {
			// Se crea el carrito
			final CarritoEntity nuevo = new CarritoEntity(idUsuario, producto.toMap());
			if(producto.getCantidad() > detallesProducto.getCantidadDisponible()) {
				final String error = String.format(PLANTILLA_CANTIDAD_SUPERA_INVENTARIO, producto.getCantidad(), detallesProducto.getCantidadDisponible(), producto.getIdProducto());
				LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, idUsuario, error);
				throw new ErrorCreandoEntidad(error);
			}
			repository.save(nuevo);
		} else {
			// Se actualiza el carrito
			final CarritoEntity actualizar = optional.get();
			if(actualizar.getProductos().containsKey(producto.getIdProducto())) {
				actualizar.getProductos().compute(
					producto.getIdProducto(), 
					(productoActual, cantidadActual) -> cantidadActual + producto.getCantidad()
				);
			} else {
				actualizar.getProductos().put(producto.getIdProducto(), producto.getCantidad());
			}

			// Chequea el inventario
			if(actualizar.getProductos().get(producto.getIdProducto()) > detallesProducto.getCantidadDisponible()) {
				final String error = String.format(PLANTILLA_CANTIDAD_SUPERA_INVENTARIO, actualizar.getProductos().get(producto.getIdProducto()), detallesProducto.getCantidadDisponible(), producto.getIdProducto());
				LOGGER.error(PLANTILLA_LOGGER_ERROR_ADICIONANDO, idUsuario, error);
				throw new ErrorCreandoEntidad(error);
			}

			// Guarda la entidad
			repository.save(actualizar);
		}
		
		// Retorna los totales actualizados
		LOGGER.info("Adición del producto '{}' al carrito del usuario '{}' exitosa", producto.getIdProducto(), idUsuario);
		return getTotalesCarrito(idUsuario);
	}

	@Override public BeanTotales actualizarProducto(String idUsuario, BeanProducto producto) {
		// Valida la cantidad
		if(producto.getCantidad() < 0) {
			final String error = "No se puede actualizar un producto con cantidades negativas";
			LOGGER.error(PLANTILLA_LOGGER_ERROR_ACTUALIZANDO, idUsuario, error);
			throw new ErrorActualizandoEntidad(error);
		}
		
		// Valida si el carrito existe
		if(!repository.existsById(idUsuario)) {
			final String error = String.format(PLANTILLA_CARRITO_NO_EXISTE, idUsuario);
			LOGGER.error(PLANTILLA_LOGGER_ERROR_ACTUALIZANDO, idUsuario, error);
			throw new ErrorEntidadNoEncontrada(error);
		}
		
		// Valida la existencia del producto en el catálogo
		final BeanDetallesProducto detallesProducto = servicioProductos.getProducto(producto.getIdProducto());
		if(detallesProducto == null) {
			final String error = String.format(PLANTILLA_PRODUCTO_NO_EXISTE, producto.getIdProducto());
			LOGGER.error(PLANTILLA_LOGGER_ERROR_ACTUALIZANDO, idUsuario, error);
			throw new ErrorEntidadNoEncontrada(error);
		}

		// Control para eliminación
		if(producto.getCantidad() == 0) {
			LOGGER.info("Se eliminará el producto '{}' del carrito de '{}' por establecer cantidad 0", producto.getIdProducto(), idUsuario);
			eliminarProducto(idUsuario, producto.getIdProducto());
			return getTotalesCarrito(idUsuario);
		}

		// Valida el inventario
		if (producto.getCantidad() > detallesProducto.getCantidadDisponible()) {
			final String error = String.format(PLANTILLA_CANTIDAD_SUPERA_INVENTARIO, producto.getCantidad(), detallesProducto.getCantidadDisponible(), producto.getIdProducto());
			LOGGER.error(PLANTILLA_LOGGER_ERROR_ACTUALIZANDO, idUsuario, error);
			throw new ErrorActualizandoEntidad(error);
		}
		
		// Consulta el producto
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		if(optional.isPresent()) {
			// Lee la entidad
			final CarritoEntity entity = optional.get();
			
			// Verifica que el producto exista
			if(!entity.getProductos().containsKey(producto.getIdProducto())) {
				final String error = String.format(PLANTILLA_PRODUCTO_NO_EN_CARRITO, producto.getIdProducto(), idUsuario);
				LOGGER.error(PLANTILLA_LOGGER_ERROR_ACTUALIZANDO, idUsuario, error);
				throw new ErrorEntidadNoEncontrada(error);
			}

			// Actualiza la cantidad
			entity.getProductos().put(producto.getIdProducto(), producto.getCantidad());
			
			// Guarda los cambios
			repository.save(entity);
		}
		
		// Retorna los totales
		LOGGER.info("Actualización del producto '{}' en el carrito del usuario '{}' exitosa", producto.getIdProducto(), idUsuario);
		return getTotalesCarrito(idUsuario);
	}

	@Override public BeanTotales eliminarProducto(String idUsuario, int idProducto) {
		// Consulta el producto
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		if(!optional.isPresent()) {
			final String error = String.format(PLANTILLA_CARRITO_NO_EXISTE, idUsuario);
			LOGGER.error("Error eliminando productos del carrito de '{}'. Error: {}", idUsuario, error);
			throw new ErrorEntidadNoEncontrada(error);
		}

		// Actualiza la cantidad
		final CarritoEntity entity = optional.get();
		if(entity.getProductos().remove(idProducto) == null) {
			final String error = String.format(PLANTILLA_PRODUCTO_NO_EN_CARRITO, idProducto, idUsuario);
			LOGGER.error("Error eliminando productos del carrito de '{}'. Error: {}", idUsuario, error);
			throw new ErrorEntidadNoEncontrada(error);
		}

		// Guarda los cambios
		if(entity.getProductos().isEmpty()) {
			repository.delete(entity);
		} else {
			repository.save(entity);
		}

		// Retorna los totales
		LOGGER.info("Eliminación del producto '{}' del carrito del usuario '{}' exitosa", idProducto, idUsuario);
		if(repository.existsById(idUsuario)) {
			return getTotalesCarrito(idUsuario);
		} else {
			LOGGER.info("Carrito del usuario '{}' quedó vacío luego de eliminar el producto '{}'", idUsuario, idProducto);
			return new BeanTotales(0, 0);
		}
	}

	@Override public BeanTotales eliminarCarrito(String idUsuario) {
		// Valida si el carrito existe
		if(!repository.existsById(idUsuario)) {
			final String error = String.format(PLANTILLA_CARRITO_NO_EXISTE, idUsuario);
			LOGGER.error("Error eliminando el carrito de '{}'. Error: {}", idUsuario, error);
			throw new ErrorEntidadNoEncontrada(error);
		}

		// Crea la entidad
		final CarritoEntity entity = new CarritoEntity(idUsuario, null);
		repository.delete(entity);
		LOGGER.info("Eliminación del carrito del usuario '{}' exitosa", idUsuario);
		return new BeanTotales(0, 0);
	}

}
