package com.sophossolutions.pocga.cassandra.service;

import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;

/**
 * Servicios para el carrito de compras
 * @author Ricardo José Ramírez Blauvelt
 */
public interface ServicioCarrito {
	
	/**
	 * Procedimiento que retorna el contenido del carrito del usuario
	 * @param idUsuario
	 * @return 
	 */
	BeanDetallesCarrito getCarrito(String idUsuario);
	
	/**
	 * Procedimiento que retorna los totales del carrito para el usuario
	 * @param idUsuario
	 * @return
	 */
	BeanTotales getTotalesCarrito(String idUsuario);
	
	/**
	 * Procedimiento que añade un producto al carrito
	 * @param idUsuario
	 * @param producto 
	 * @return  
	 */
	BeanTotales adicionarProducto(String idUsuario, BeanProducto producto);
	
	/**
	 * Procedimiento que actualiza las cantidades de una producto que ya está en el carrito
	 * @param idUsuario
	 * @param producto 
	 * @return  
	 */
	BeanTotales actualizarProducto(String idUsuario, BeanProducto producto);
	
	/**
	 * Procedimiento que elimina un producto del carrito
	 * @param idUsuario
	 * @param idProducto 
	 * @return  
	 */
	BeanTotales eliminarProducto(String idUsuario, int idProducto);

	/**
	 * Procedimiento que elimina el contenido del carrito de un usuario
	 * @param idUsuario 
	 * @return  
	 */
	BeanTotales eliminarCarrito(String idUsuario);

}
