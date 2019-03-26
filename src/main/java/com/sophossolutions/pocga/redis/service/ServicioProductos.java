package com.sophossolutions.pocga.redis.service;

import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import com.sophossolutions.pocga.beans.BeanProducto;
import java.util.List;
import java.util.Map;

/**
 * Servicios para los productos
 * @author Ricardo José Ramírez Blauvelt
 */
public interface ServicioProductos {

	/**
	 * Procedimiento para consultar un producto
	 * @param idProducto
	 * @return 
	 */
	BeanDetallesProducto getProducto(int idProducto);

	/**
	 * Procedimiento que determina si un producto existe en el catálogo
	 * @param idProducto
	 * @return 
	 */
	boolean isProductoEnCatalogo(int idProducto);

	/**
	 * Procedimiento para guardar un producto
	 * @param producto 
	 */
	void setProducto(BeanDetallesProducto producto);

	/**
	 * Procedimiento que crea una lista de productos y cantidades, a partir del mapa que 
	 * que lo representa (producto -> cantidad)
	 * @param productos
	 * @return 
	 */
	List<BeanCantidadProducto> fromMapProductos(Map<Integer, Integer> productos);

	/**
	 * Procedimiento que genera un mapa de representación a partir de los objetos
	 * @param productos
	 * @return 
	 */
	Map<Integer, Integer> toMapProductos(List<BeanCantidadProducto> productos);

	/**
	 * Procedimiento que crea un objeto de producto a partir del bean que lo representa
	 * @param producto
	 * @return 
	 */
	BeanCantidadProducto fromBeanProducto(BeanProducto producto);

}
