package com.sophossolutions.pocga.beans;

/**
 * Bean para transporte de cantidades
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanCantidad {

	private int cantidad;

	public BeanCantidad() {
	}

	public BeanCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	@Override public String toString() {
		return "BeanCantidad{" + "cantidad=" + cantidad + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 97 * hash + this.cantidad;
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
		final BeanCantidad other = (BeanCantidad) obj;
		return this.cantidad == other.cantidad;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

}
