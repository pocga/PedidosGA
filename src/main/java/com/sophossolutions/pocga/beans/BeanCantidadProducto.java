package com.sophossolutions.pocga.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 * Bean para almacenar el producto y la cantidad
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanCantidadProducto implements Comparable<BeanCantidadProducto>, Serializable {
	
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
	
	@Override public int compareTo(BeanCantidadProducto o) {
		return this.getProducto().getIdProducto() - o.getProducto().getIdProducto();
	}

}
