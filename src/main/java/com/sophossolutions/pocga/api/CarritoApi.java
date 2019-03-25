package com.sophossolutions.pocga.api;

import com.sophossolutions.pocga.api.exceptions.ErrorActualizandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorCreandoEntidad;
import com.sophossolutions.pocga.api.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.api.exceptions.ErrorListadoEntidadesVacio;
import com.sophossolutions.pocga.beans.BeanCantidad;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.cassandra.service.ServicioCarrito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para la API de CarritoApi
 * @author Ricardo José Ramírez Blauvelt
 */
@RestController
@RequestMapping("/carrito")
public class CarritoApi {

	/** Logger de eventos */
	private static final Logger LOGGER = LoggerFactory.getLogger(CarritoApi.class);

	@Autowired
	private ServicioCarrito servicio;
	
	/**
	 * Consulta del carrito de un usuario
	 * @param idUsuario
	 * @return 
	 */
	@GetMapping("/{idUsuario}/productos")
	public ResponseEntity<BeanDetallesCarrito> getCarrito(@PathVariable String idUsuario) {
		final BeanDetallesCarrito carritoUsuario = servicio.getCarrito(idUsuario);
		if(carritoUsuario != null) {
			LOGGER.info("Consulta del carrito del usuario '{}' exitosa", idUsuario);
			return new ResponseEntity<>(carritoUsuario, HttpStatus.OK);
		} else {
			LOGGER.error("El usuario '{}' no tiene un carrito en el sistema -> {}", idUsuario, HttpStatus.NOT_FOUND);
			throw new ErrorListadoEntidadesVacio("El usuario {" + idUsuario + "} no tiene productos en el carrito de compras");
		}
	}

	/**
	 * Consulta los totales del carrito de un usuario
	 * @param idUsuario
	 * @return 
	 */
	@GetMapping("/{idUsuario}/productos/totales")
	public ResponseEntity<BeanTotales> getTotalesCarrito(@PathVariable String idUsuario) {
		final BeanTotales totales = servicio.getTotalesCarrito(idUsuario);
		if (totales.getTotalCantidad() > 0) {
			LOGGER.info("Consulta de los totales del carrito del usuario '{}' exitosa", idUsuario);
			return new ResponseEntity<>(totales, HttpStatus.OK);
		} else {
			LOGGER.error("El usuario {} no tiene un carrito en el sistema -> {}", idUsuario, HttpStatus.NOT_FOUND);
			throw new ErrorEntidadNoEncontrada("El usuario {" + idUsuario + "} no tiene productos en el carrito de compras");
		}
	}

	/**
	 * Adiciona un producto al carrito de un usuario
	 * @param idUsuario
	 * @param producto 
	 * @return  
	 */
	@PostMapping("/{idUsuario}/productos")
	public ResponseEntity<BeanTotales> addProducto(@PathVariable String idUsuario, @RequestBody BeanProducto producto) {
		try {
			final BeanTotales nuevosTotales = servicio.adicionarProducto(idUsuario, producto);
			LOGGER.info("Producto '{}' adicionado correctamente al carrito del usuario '{}'", producto.getIdProducto(), idUsuario);
			return new ResponseEntity<>(nuevosTotales, HttpStatus.CREATED);
		} catch (ErrorEntidadNoEncontrada eene) {
			LOGGER.error(eene.getLocalizedMessage() + ". No se puede adicionar el producto {} al carrito de {} -> {}", producto, idUsuario, HttpStatus.NOT_FOUND);
			throw eene;
		} catch (ErrorCreandoEntidad ece) {
			LOGGER.error(ece.getLocalizedMessage() + ". No se puede adicionar el producto {} al carrito de {} -> {}", producto, idUsuario, HttpStatus.UNPROCESSABLE_ENTITY);
			throw ece;
		}
	}

	/**
	 * Actualiza la cantidad de un producto, vía "body" con producto y cantidad
	 * @param idUsuario
	 * @param producto 
	 * @return  
	 */
	@PutMapping("/{idUsuario}/productos")
	public ResponseEntity<BeanTotales> setProducto(@PathVariable String idUsuario, @RequestBody BeanProducto producto) {
		try {
			final BeanTotales nuevosTotales = servicio.actualizarProducto(idUsuario, producto);
			return new ResponseEntity<>(nuevosTotales, HttpStatus.OK);
		} catch (ErrorEntidadNoEncontrada eene) {
			LOGGER.error(eene.getLocalizedMessage() + ". No se puede actualizar el carrito de {} -> {}", idUsuario, HttpStatus.NOT_FOUND);
			throw new ErrorActualizandoEntidad(eene.getLocalizedMessage());
		} catch (ErrorActualizandoEntidad eae) {
			LOGGER.error(eae.getLocalizedMessage() + ". No se puede actualizar el producto {} del carrito de {} -> {}", producto, idUsuario, HttpStatus.UNPROCESSABLE_ENTITY);
			throw eae;
		}
	}

	/**
	 * Actualiza la cantidad de un producto, vía "body" con producto y cantidad
	 * @param idUsuario 
	 * @param idProducto 
	 * @param cantidad 
	 * @return  
	 */
	@PutMapping("/{idUsuario}/productos/{idProducto}")
	public ResponseEntity<BeanTotales> setProducto(@PathVariable String idUsuario, @PathVariable int idProducto, @RequestBody BeanCantidad cantidad) {
		try {
			final BeanTotales nuevosTotales = servicio.actualizarProducto(idUsuario, new BeanProducto(idProducto, cantidad.getCantidad()));
			return new ResponseEntity<>(nuevosTotales, HttpStatus.OK);
		} catch (ErrorEntidadNoEncontrada eene) {
			LOGGER.error(eene.getLocalizedMessage() + ". No se puede actualizar el carrito de {} -> {}", idUsuario, HttpStatus.NOT_FOUND);
			throw new ErrorActualizandoEntidad(eene.getLocalizedMessage());
		}
	}

	/**
	 * Elimina un producto del carrito
	 * @param idUsuario
	 * @param idProducto 
	 * @return  
	 */
	@DeleteMapping("/{idUsuario}/productos/{idProducto}")
	public ResponseEntity<BeanTotales> removeProducto(@PathVariable String idUsuario, @PathVariable int idProducto) {
		try {
			final BeanTotales nuevosTotales = servicio.eliminarProducto(idUsuario, idProducto);
			LOGGER.info("Eliminación del producto '{}' exitosa", idProducto);
			return new ResponseEntity<>(nuevosTotales, HttpStatus.OK);
		} catch (ErrorEntidadNoEncontrada iae) {
			LOGGER.error(iae.getLocalizedMessage() + ". No se eliminó el producto {} para el usuario {} -> {}", idProducto, idUsuario, HttpStatus.NOT_FOUND);
			throw iae;
		}
	}

	/**
	 * Vacía el carrito de un usuario
	 * @param idUsuario 
	 * @return  
	 */
	@DeleteMapping("/{idUsuario}/productos")
	public ResponseEntity<BeanTotales> removeCarrito(@PathVariable String idUsuario) {
		try {
			final BeanTotales nuevosTotales = servicio.eliminarCarrito(idUsuario);
			LOGGER.info("Eliminación del carrito del usuario '{}' exitosa", idUsuario);
			return new ResponseEntity<>(nuevosTotales, HttpStatus.OK);
		} catch (ErrorEntidadNoEncontrada iae) {
			LOGGER.error(iae.getLocalizedMessage() + ". No se eliminó el carrito para el usuario {} -> {}", idUsuario, HttpStatus.NOT_FOUND);
			return new ResponseEntity<>(new BeanTotales(), HttpStatus.NOT_FOUND);
		}
	}

}
