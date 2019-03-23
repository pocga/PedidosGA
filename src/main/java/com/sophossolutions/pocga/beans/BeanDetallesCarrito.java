package com.sophossolutions.pocga.beans;

import java.util.List;
import java.util.Objects;

/**
 * Bean con todos los detalles del carrito de compras de un usuario
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanDetallesCarrito {
	
	private String idUsuario;
	private List<BeanCantidadProducto> productos;
	private BeanTotales totales;

	public BeanDetallesCarrito() {
	}

	public BeanDetallesCarrito(String idUsuario, List<BeanCantidadProducto> productos, BeanTotales totales) {
		this.idUsuario = idUsuario;
		this.productos = productos;
		this.totales = totales;
	}

	@Override public String toString() {
		return "BeanDetallesCarrito{" + "idUsuario=" + idUsuario + ", productos=" + productos + ", totales=" + totales + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Objects.hashCode(this.idUsuario);
		hash = 31 * hash + Objects.hashCode(this.productos);
		hash = 31 * hash + Objects.hashCode(this.totales);
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
		final BeanDetallesCarrito other = (BeanDetallesCarrito) obj;
		if (!Objects.equals(this.idUsuario, other.idUsuario)) {
			return false;
		}
		if (!Objects.equals(this.productos, other.productos)) {
			return false;
		}
		if (!Objects.equals(this.totales, other.totales)) {
			return false;
		}
		return true;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public List<BeanCantidadProducto> getProductos() {
		return productos;
	}

	public void setProductos(List<BeanCantidadProducto> productos) {
		this.productos = productos;
	}

	public BeanTotales getTotales() {
		return totales;
	}

	public void setTotales(BeanTotales totales) {
		this.totales = totales;
	}

}
