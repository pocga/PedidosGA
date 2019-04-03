package com.sophossolutions.pocga.beans;

import java.util.List;
import java.util.Objects;

/**
 * Bean con todos los detalles del carrito de compras de un usuario
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanDetallesCarrito {
	
	private BeanUsuario usuario;
	private List<BeanCantidadProducto> productos;
	private BeanTotales totales;

	public BeanDetallesCarrito() {
	}

	@Override public String toString() {
		return "BeanDetallesCarrito{" + "idUsuario=" + usuario + ", productos=" + productos + ", totales=" + totales + '}';
	}

	@Override public int hashCode() {
		int hash = 5;
		hash = 79 * hash + Objects.hashCode(this.usuario);
		hash = 79 * hash + Objects.hashCode(this.productos);
		hash = 79 * hash + Objects.hashCode(this.totales);
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
		if (!Objects.equals(this.usuario, other.usuario)) {
			return false;
		}
		if (!Objects.equals(this.productos, other.productos)) {
			return false;
		}
		return Objects.equals(this.totales, other.totales);
	}	

	public BeanUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(BeanUsuario usuario) {
		this.usuario = usuario;
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
