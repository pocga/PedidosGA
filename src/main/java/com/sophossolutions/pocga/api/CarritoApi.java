package com.sophossolutions.pocga.api;

import com.sophossolutions.pocga.beans.BeanCantidad;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.cassandra.service.ServicioCarrito;
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
	public ResponseEntity<BeanDetallesCarrito> getCarrito(@PathVariable String idUsuario) {
		return new ResponseEntity<>(servicio.getCarrito(idUsuario), HttpStatus.OK);
	}

	/**
	 * Consulta los totales del carrito de un usuario
	 * @param idUsuario
	 * @return 
	 */
	@GetMapping("/{idUsuario}/productos/totales")
	public ResponseEntity<BeanTotales> getTotalesCarrito(@PathVariable String idUsuario) {
		return new ResponseEntity<>(servicio.getTotalesCarrito(idUsuario), HttpStatus.OK);
	}

	/**
	 * Adiciona un producto al carrito de un usuario
	 * @param idUsuario
	 * @param producto 
	 * @return  
	 */
	@PostMapping("/{idUsuario}/productos")
	public ResponseEntity<BeanTotales> addProducto(@PathVariable String idUsuario, @RequestBody BeanProducto producto) {
		return new ResponseEntity<>(servicio.adicionarProducto(idUsuario, producto), HttpStatus.CREATED);
	}

	/**
	 * Actualiza la cantidad de un producto, vía "body" con producto y cantidad
	 * @param idUsuario
	 * @param producto 
	 * @return  
	 */
	@PutMapping("/{idUsuario}/productos")
	public ResponseEntity<BeanTotales> setProducto(@PathVariable String idUsuario, @RequestBody BeanProducto producto) {
		return new ResponseEntity<>(servicio.actualizarProducto(idUsuario, producto), HttpStatus.OK);
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
		return new ResponseEntity<>(servicio.actualizarProducto(idUsuario, new BeanProducto(idProducto, cantidad.getCantidad())), HttpStatus.OK);
	}

	/**
	 * Elimina un producto del carrito
	 * @param idUsuario
	 * @param idProducto 
	 * @return  
	 */
	@DeleteMapping("/{idUsuario}/productos/{idProducto}")
	public ResponseEntity<BeanTotales> removeProducto(@PathVariable String idUsuario, @PathVariable int idProducto) {
		return new ResponseEntity<>(servicio.eliminarProducto(idUsuario, idProducto), HttpStatus.OK);
	}

	/**
	 * Vacía el carrito de un usuario
	 * @param idUsuario 
	 * @return  
	 */
	@DeleteMapping("/{idUsuario}/productos")
	public ResponseEntity<BeanTotales> removeProducto(@PathVariable String idUsuario) {
		return new ResponseEntity<>(servicio.eliminarCarrito(idUsuario), HttpStatus.OK);
	}

}
