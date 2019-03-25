package com.sophossolutions.pocga.beans;

import java.util.Map;

/**
 * Bean que contiene los detalles de un producto
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanProducto {
	
	private int idProducto;
	private int cantidad;

	public BeanProducto() {
	}

	public BeanProducto(int idProducto, int cantidadPedida) {
		this.idProducto = idProducto;
		this.cantidad = cantidadPedida;
	}
	
	@Override public String toString() {
		return "BeanProducto{" + "idProducto=" + idProducto + ", cantidadPedida=" + cantidad + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 47 * hash + this.idProducto;
		hash = 47 * hash + this.cantidad;
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
		final BeanProducto other = (BeanProducto) obj;
		if (this.idProducto != other.idProducto) {
			return false;
		}
		return this.cantidad == other.cantidad;
	}

	

	public int getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	/**
	 * Se crea el mapa de productos a partir del objeto
	 * @param producto
 	 * @return 
	 */
	public static Map<Integer, Integer> toMap(BeanProducto producto) {
		return Map.of(producto.getIdProducto(), producto.getCantidad());
	}

}
