package com.sophossolutions.pocga.beans;

/**
 * Bean para los totales del carrito
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanTotales {

	private int totalCantidad;
	private int totalPrecio;

	public BeanTotales() {
	}

	public BeanTotales(int totalCantidad, int totalPrecio) {
		this.totalCantidad = totalCantidad;
		this.totalPrecio = totalPrecio;
	}

	@Override public String toString() {
		return "BeanTotales{" + "totalCantidad=" + totalCantidad + ", totalPrecio=" + totalPrecio + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 97 * hash + this.totalCantidad;
		hash = 97 * hash + this.totalPrecio;
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
		final BeanTotales other = (BeanTotales) obj;
		if (this.totalCantidad != other.totalCantidad) {
			return false;
		}
		return this.totalPrecio == other.totalPrecio;
	}

	public int getTotalCantidad() {
		return totalCantidad;
	}

	public void setTotalCantidad(int totalCantidad) {
		this.totalCantidad = totalCantidad;
	}

	public int getTotalPrecio() {
		return totalPrecio;
	}

	public void setTotalPrecio(int totalPrecio) {
		this.totalPrecio = totalPrecio;
	}

}
