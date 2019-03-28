package com.sophossolutions.pocga.cassandra.service;

import com.sophossolutions.pocga.api.exceptions.ErrorActualizandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorCreandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.cassandra.entity.CarritoEntity;
import com.sophossolutions.pocga.cassandra.repository.CarritoRepository;
import com.sophossolutions.pocga.redis.service.ServicioProductos;
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

	@Autowired
	private CarritoRepository repository;
	
	@Autowired
	private ServicioProductos servicioProductos;
	
	@Override public BeanDetallesCarrito getCarrito(String idUsuario) {
		// Consulta
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		if(!optional.isPresent()) {
			LOGGER.info("Carrito no encontrado para usuario {}", idUsuario);
			return null;
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
				throw new ErrorEntidadNoEncontrada("El producto {" + producto + "} no existe en el catálogo");
			}
			bcp.setProducto(detallesProducto);
			listaProductos.add(bcp);
		});
		
		// Arma y entrega los detalles
		final BeanDetallesCarrito detalles = new BeanDetallesCarrito();
		detalles.setIdUsuario(idUsuario);
		detalles.setProductos(listaProductos);
		detalles.setTotales(getTotalesCarrito(listaProductos));
		return detalles;
	}

	@Override public BeanTotales getTotalesCarrito(String idUsuario) {
		// Trae todos los productos en el carrito
		final BeanDetallesCarrito detalles = getCarrito(idUsuario);
		if(detalles == null) {
			return new BeanTotales(0, 0);
		}
		
		// Calcula los totales
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
			throw new ErrorCreandoEntidad("No se puede adicionar un producto con cantidad inferior a 1");
		}

		// Valida la existencia del producto en el catálogo
		final BeanDetallesProducto detallesProducto = servicioProductos.getProducto(producto.getIdProducto());
		if(detallesProducto == null) {
			throw new ErrorEntidadNoEncontrada("El producto {" + producto.getIdProducto() + "} no está registrado en el sistema");
		}
		
		// Consulta el carrito
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		
		// Determina si se crea el carrito o se adiciona un producto
		if(!optional.isPresent()) {
			// Se crea el carrito
			final CarritoEntity nuevo = new CarritoEntity(idUsuario, producto.toMap());
			if(producto.getCantidad() > detallesProducto.getCantidadDisponible()) {
				throw new ErrorCreandoEntidad("Intentando adicionar más unidades {" + producto.getCantidad() + "} de las disponibles en el inventario {" + detallesProducto.getCantidadDisponible() + "} para el producto {" + producto.getIdProducto() + "}");
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
				throw new ErrorCreandoEntidad("Intentando adicionar más unidades {" + actualizar.getProductos().get(producto.getIdProducto()) + "} de las disponibles en el inventario {" + detallesProducto.getCantidadDisponible() + "} para el producto {" + producto.getIdProducto() + "}");
			}

			// Guarda la entidad
			repository.save(actualizar);
		}
		
		// Retorna los totales actualizados
		return getTotalesCarrito(idUsuario);
	}

	@Override public BeanTotales actualizarProducto(String idUsuario, BeanProducto producto) {
		// Valida la cantidad
		if(producto.getCantidad() < 0) {
			throw new ErrorActualizandoEntidad("No se puede actualizar un producto con cantidades negativas");
		}
		
		// Valida si el carrito existe
		if(!repository.existsById(idUsuario)) {
			throw new ErrorEntidadNoEncontrada("No existe el carrito de compras del usuario {" + idUsuario +"}");
		}
		
		// Valida la existencia del producto en el catálogo
		final BeanDetallesProducto detallesProducto = servicioProductos.getProducto(producto.getIdProducto());
		if(detallesProducto == null) {
			throw new ErrorEntidadNoEncontrada("El producto {" + producto.getIdProducto() + "} no está registrado en el sistema");
		}

		// Control para eliminación
		if(producto.getCantidad() == 0) {
			LOGGER.info("Se eliminará el producto {} del carrito de {} por poner cantidad 0", producto.getIdProducto(), idUsuario);
			eliminarProducto(idUsuario, producto.getIdProducto());
			return getTotalesCarrito(idUsuario);
		}

		// Valida el inventario
		if (producto.getCantidad() > detallesProducto.getCantidadDisponible()) {
			throw new ErrorActualizandoEntidad("Intentando establecer más unidades {" + producto.getCantidad() + "} de las disponibles en el inventario {" + detallesProducto.getCantidadDisponible() + "} para el producto {" + producto.getIdProducto() + "}");
		}
		
		// Consulta el producto
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		if(optional.isPresent()) {
			// Lee la entidad
			final CarritoEntity entity = optional.get();
			
			// Verifica que el producto exista
			if(!entity.getProductos().containsKey(producto.getIdProducto())) {
				throw new ErrorEntidadNoEncontrada("No existe el producto {" + producto.getIdProducto() + "} en el carrito de compras del usuario {" + idUsuario + "}");
			}

			// Actualiza la cantidad
			entity.getProductos().put(producto.getIdProducto(), producto.getCantidad());
			
			// Guarda los cambios
			repository.save(entity);
		}
		
		// Retorna los totales
		return getTotalesCarrito(idUsuario);
	}

	@Override public BeanTotales eliminarProducto(String idUsuario, int idProducto) {
		// Consulta el producto
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		if(!optional.isPresent()) {
			throw new ErrorEntidadNoEncontrada("No se encontró un carrito para el usuario {" + idUsuario + "}");
		}

		// Actualiza la cantidad
		final CarritoEntity entity = optional.get();
		if(entity.getProductos().remove(idProducto) == null) {
			throw new ErrorEntidadNoEncontrada("No se encontró el producto {" + idProducto + "} en el carrito del usuario {" + idUsuario + "}");
		}

		// Guarda los cambios
		if(entity.getProductos().isEmpty()) {
			repository.delete(entity);
		} else {
			repository.save(entity);
		}

		// Retorna los totales
		return getTotalesCarrito(idUsuario);
	}

	@Override public BeanTotales eliminarCarrito(String idUsuario) {
		// Valida si el carrito existe
		if(!repository.existsById(idUsuario)) {
			throw new ErrorEntidadNoEncontrada("No existe el carrito de compras del usuario {" + idUsuario +"}");
		}

		// Crea la entidad
		final CarritoEntity entity = new CarritoEntity(idUsuario, null);
		repository.delete(entity);
		return getTotalesCarrito(idUsuario);
	}

}
