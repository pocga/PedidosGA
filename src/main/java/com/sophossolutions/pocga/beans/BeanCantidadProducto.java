package com.sophossolutions.pocga.beans;

import com.sophossolutions.pocga.rest.ConsumirCatalogoApi;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Bean para almacenar el producto y la cantidad
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanCantidadProducto {
	
	private BeanDetallesProducto producto;
	private int cantidad;

	public BeanCantidadProducto() {
	}

	public BeanCantidadProducto(BeanDetallesProducto producto, int cantidad) {
		this.producto = producto;
		this.cantidad = cantidad;
	}

	@Override public String toString() {
		return "BeanCantidadProducto{" + "producto=" + producto + ", cantidad=" + cantidad + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.producto);
		hash = 67 * hash + this.cantidad;
		return hash;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BeanCantidadProducto other = (BeanCantidadProducto) obj;
		if (this.cantidad != other.cantidad) {
			return false;
		}
		return Objects.equals(this.producto, other.producto);
	}

	public BeanDetallesProducto getProducto() {
		return producto;
	}

	public void setProducto(BeanDetallesProducto producto) {
		this.producto = producto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	/**
	 * Crea una lista de objetos a partir del mapa de producto -> cantidad
	 * @param productos
	 * @return 
	 */
	public static List<BeanCantidadProducto> fromMapProductos(Map<Integer, Integer> productos) {
		final List<BeanCantidadProducto> listaProductos = new ArrayList<>();
		productos.forEach((producto, cantidad) -> {
			final BeanCantidadProducto bcp = new BeanCantidadProducto();
			bcp.setCantidad(cantidad);
			bcp.setProducto(ConsumirCatalogoApi.getProducto(producto));
			listaProductos.add(bcp);
		});
		return listaProductos;
	}
	
	/**
	 * Crea un objeto con detalles a partir del objeto simple
	 * @param producto
	 * @return 
	 */
	public static BeanCantidadProducto fromBeanProducto(BeanProducto producto) {
		final BeanCantidadProducto bcp = new BeanCantidadProducto();
		bcp.setCantidad(producto.getCantidadPedida());
		bcp.setProducto(ConsumirCatalogoApi.getProducto(producto.getIdProducto()));
		return bcp;
	}

	/**
	 * Procedimiento que genera el mapa de productos a partir de la lista detallada
	 * @param productos
	 * @return 
	 */
	public static Map<Integer, Integer> toMapProductos(List<BeanCantidadProducto> productos) {
		// Carga los productos
		return productos.stream()
			.collect(
				Collectors.toMap(
					bcp -> bcp.getProducto().getIdProducto(), 
					bcp -> bcp.getCantidad()
				)
			)
		;
	}

}
