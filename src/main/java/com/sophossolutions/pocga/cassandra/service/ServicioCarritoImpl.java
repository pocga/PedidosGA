package com.sophossolutions.pocga.cassandra.service;

import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.cassandra.entity.CarritoEntity;
import com.sophossolutions.pocga.cassandra.repository.CarritoRepository;
import com.sophossolutions.pocga.rest.ConsumirCatalogoApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
			bcp.setProducto(ConsumirCatalogoApi.getProducto(producto));
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
		// Consulta el carrito
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		
		// ¿No tiene productos?
		if(!optional.isPresent()) {
			final Map<Integer, Integer> mapaProducto = new HashMap<>();
			mapaProducto.put(producto.getIdProducto(), producto.getCantidadPedida());
			final CarritoEntity nuevo = new CarritoEntity(idUsuario, mapaProducto);
			repository.save(nuevo);
		} else {
			final CarritoEntity actualizar = optional.get();
			actualizar.getProductos().put(producto.getIdProducto(), producto.getCantidadPedida());
			repository.save(actualizar);
		}
		
		// Retorna los totales actualizados
		return getTotalesCarrito(idUsuario);
	}

	@Override public BeanTotales actualizarProducto(String idUsuario, BeanProducto producto) {
		// Control para eliminación
		if(producto.getCantidadPedida() == 0) {
			LOGGER.info("Se eliminará el producto {} del carrito de {} por poner cantidad 0", producto.getIdProducto(), idUsuario);
			eliminarProducto(idUsuario, producto.getIdProducto());
			return getTotalesCarrito(idUsuario);
		}

		// Consulta el producto
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		if(optional.isPresent()) {
			// Actualiza la cantidad
			final CarritoEntity entity = optional.get();
			entity.getProductos().put(producto.getIdProducto(), producto.getCantidadPedida());
			
			// Guarda los cambios
			repository.save(entity);
		}
		
		// Retorna los totales
		return getTotalesCarrito(idUsuario);
	}

	@Override public BeanTotales eliminarProducto(String idUsuario, int idProducto) {
		// Consulta el producto
		final Optional<CarritoEntity> optional = repository.findById(idUsuario);
		if(optional.isPresent()) {
			// Actualiza la cantidad
			final CarritoEntity entity = optional.get();
			entity.getProductos().remove(idProducto);
			
			// Guarda los cambios
			if(entity.getProductos().isEmpty()) {
				repository.delete(entity);
			} else {
				repository.save(entity);
			}
		}
		
		// Retorna los totales
		return getTotalesCarrito(idUsuario);
	}

	@Override public BeanTotales eliminarCarrito(String idUsuario) {
		// Crea la entidad
		final CarritoEntity entity = new CarritoEntity(idUsuario, null);
		repository.delete(entity);
		return getTotalesCarrito(idUsuario);
	}

	
}
