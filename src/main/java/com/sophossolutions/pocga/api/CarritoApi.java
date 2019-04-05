package com.sophossolutions.pocga.api;

import com.sophossolutions.pocga.beans.BeanCantidad;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.model.ServicioCarrito;
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

	@Autowired
	private ServicioCarrito servicio;
	
	/**
	 * Consulta del carrito de un usuario
	 * @param idUsuario
	 * @return 
	 */
	@GetMapping("/{idUsuario}/productos")
	public BeanDetallesCarrito getCarrito(@PathVariable String idUsuario) {
		return servicio.getCarrito(idUsuario);
	}

	/**
	 * Consulta los totales del carrito de un usuario
	 * @param idUsuario
	 * @return 
	 */
	@GetMapping("/{idUsuario}/productos/totales")
	public BeanTotales getTotalesCarrito(@PathVariable String idUsuario) {
		return servicio.getTotalesCarrito(idUsuario);
	}

	/**
	 * Adiciona un producto al carrito de un usuario
	 * @param idUsuario
	 * @param producto 
	 * @return  
	 */
	@PostMapping("/{idUsuario}/productos")
	public ResponseEntity<BeanTotales> addProducto(@PathVariable String idUsuario, @RequestBody BeanProducto producto) {
		final BeanTotales nuevosTotales = servicio.adicionarProducto(idUsuario, producto);
		return new ResponseEntity<>(nuevosTotales, HttpStatus.CREATED);
	}

	/**
	 * Actualiza la cantidad de un producto, vía "body" con producto y cantidad
	 * @param idUsuario
	 * @param producto 
	 * @return  
	 */
	@PutMapping("/{idUsuario}/productos")
	public BeanTotales setProducto(@PathVariable String idUsuario, @RequestBody BeanProducto producto) {
		return servicio.actualizarProducto(idUsuario, producto);
	}

	/**
	 * Actualiza la cantidad de un producto, vía "body" con producto y cantidad
	 * @param idUsuario 
	 * @param idProducto 
	 * @param cantidad 
	 * @return  
	 */
	@PutMapping("/{idUsuario}/productos/{idProducto}")
	public BeanTotales setProducto(@PathVariable String idUsuario, @PathVariable int idProducto, @RequestBody BeanCantidad cantidad) {
		return servicio.actualizarProducto(idUsuario, new BeanProducto(idProducto, cantidad.getCantidad()));
	}

	/**
	 * Elimina un producto del carrito
	 * @param idUsuario
	 * @param idProducto 
	 * @return  
	 */
	@DeleteMapping("/{idUsuario}/productos/{idProducto}")
	public BeanTotales removeProducto(@PathVariable String idUsuario, @PathVariable int idProducto) {
		return servicio.eliminarProducto(idUsuario, idProducto);
	}

	/**
	 * Vacía el carrito de un usuario
	 * @param idUsuario 
	 * @return  
	 */
	@DeleteMapping("/{idUsuario}/productos")
	public BeanTotales removeCarrito(@PathVariable String idUsuario) {
		return servicio.eliminarCarrito(idUsuario);
	}

}
