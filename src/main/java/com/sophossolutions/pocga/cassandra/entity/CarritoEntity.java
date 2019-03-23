package com.sophossolutions.pocga.cassandra.entity;

import java.util.Map;
import java.util.Objects;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Entidad del carrito de compras
 * @author Ricardo José Ramírez Blauvelt
 */
@Table(value = "carrito")
public class CarritoEntity {
	
	@PrimaryKey(value = "id_usuario")
	private String idUsuario;

	@Column(value = "productos")
	private Map<Integer, Integer> productos;

	public CarritoEntity() {
	}

	public CarritoEntity(String idUsuario, Map<Integer, Integer> productos) {
		this.idUsuario = idUsuario;
		this.productos = productos;
	}

	@Override public String toString() {
		return "CarritoEntity{" + "idUsuario=" + idUsuario + ", productos=" + productos + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.idUsuario);
		hash = 37 * hash + Objects.hashCode(this.productos);
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
		final CarritoEntity other = (CarritoEntity) obj;
		if (!Objects.equals(this.idUsuario, other.idUsuario)) {
			return false;
		}
		return Objects.equals(this.productos, other.productos);
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Map<Integer, Integer> getProductos() {
		return productos;
	}

	public void setProductos(Map<Integer, Integer> productos) {
		this.productos = productos;
	}

}
