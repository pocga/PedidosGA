package com.sophossolutions.pocga.beans;

/**
 * Bean que contiene los detalles de un producto
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanProducto {
	
	private int idProducto;
	private int cantidadPedida;

	public BeanProducto() {
	}

	public BeanProducto(int idProducto, int cantidadPedida) {
		this.idProducto = idProducto;
		this.cantidadPedida = cantidadPedida;
	}
	
	@Override public String toString() {
		return "BeanProducto{" + "idProducto=" + idProducto + ", cantidadPedida=" + cantidadPedida + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 47 * hash + this.idProducto;
		hash = 47 * hash + this.cantidadPedida;
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
		return this.cantidadPedida == other.cantidadPedida;
	}

	

	public int getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(int idProducto) {
		this.idProducto = idProducto;
	}

	public int getCantidadPedida() {
		return cantidadPedida;
	}

	public void setCantidadPedida(int cantidadPedida) {
		this.cantidadPedida = cantidadPedida;
	}

}
