package com.sophossolutions.pocga.redis.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

/**
 * Entidad para acceder a los productos en Redis
 * @author Ricardo José Ramírez Blauvelt
 */
@RedisHash("producto")
public class ProductoEntity implements Serializable {

	@Id
	private String idProducto;
	private String categoria;
	private int cantidadDisponible;
	private int precio;
	private String descripcion;
	private String imagen;
	private String miniatura;
	
	@TimeToLive(unit = TimeUnit.SECONDS)
	private long timeToLive = 10;

	public ProductoEntity() {
	}

	public ProductoEntity(String idProducto, String categoria, int cantidadDisponible, int precio, String descripcion, String imagen, String miniatura) {
		this.idProducto = idProducto;
		this.categoria = categoria;
		this.cantidadDisponible = cantidadDisponible;
		this.precio = precio;
		this.descripcion = descripcion;
		this.imagen = imagen;
		this.miniatura = miniatura;
	}

	@Override public String toString() {
		return "ProductoEntity{" + "idProducto=" + idProducto + ", categoria=" + categoria + ", cantidadDisponible=" + cantidadDisponible + ", precio=" + precio + ", descripcion=" + descripcion + ", imagen=" + imagen + ", miniatura=" + miniatura + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 83 * hash + Objects.hashCode(this.idProducto);
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
		final ProductoEntity other = (ProductoEntity) obj;
		return Objects.equals(this.idProducto, other.idProducto);
	}
	
	public String getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(String idProducto) {
		this.idProducto = idProducto;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public int getCantidadDisponible() {
		return cantidadDisponible;
	}

	public void setCantidadDisponible(int cantidadDisponible) {
		this.cantidadDisponible = cantidadDisponible;
	}

	public int getPrecio() {
		return precio;
	}

	public void setPrecio(int precio) {
		this.precio = precio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public String getMiniatura() {
		return miniatura;
	}

	public void setMiniatura(String miniatura) {
		this.miniatura = miniatura;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

}
