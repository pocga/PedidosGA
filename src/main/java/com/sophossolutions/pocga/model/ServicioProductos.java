package com.sophossolutions.pocga.model;

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
	 * Procedimiento para consultar un producto del catálogo, bien sea del cache o de la API
	 * @param idProducto
	 * @return 
	 */
	BeanDetallesProducto getProducto(int idProducto);

	/**
	 * Procedimiento para guardar un producto en el cache
	 * @param producto 
	 */
	void setProducto(BeanDetallesProducto producto);
	
	/**
	 * Procedimiento que elimina un producto del cache
	 * @param idProducto 
	 */
	void removeProducto(int idProducto);
	
	/**
	 * Procedimiento que limpia el cache de productos
	 */
	void clearCacheProductos();
	
	/**
	 * Consulta el producto en la API remota
	 * @param idProducto
	 * @return
	 */
	BeanDetallesProducto getProductoDesdeApi(int idProducto);

	/**
	 * Procedimiento que crea una lista de productos y cantidades, a partir del mapa que 
	 * que lo representa (producto -> cantidad)
	 * @param productos
	 * @return 
	 */
	List<BeanCantidadProducto> fromMapProductos(Map<Integer, Integer> productos);

	/**
	 * Procedimiento que crea un objeto de producto a partir del bean que lo representa
	 * @param producto
	 * @return 
	 */
	BeanCantidadProducto fromBeanProducto(BeanProducto producto);

}
