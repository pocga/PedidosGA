package com.sophossolutions.pocga.beans;

import com.sophossolutions.pocga.redis.entity.ProductoEntity;

/**
 * Bean con todos los detalles de un producto
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanDetallesProducto {
	
	private int idProducto;
	private String categoria;
	private int cantidadDisponible;
	private int precio;
	private String descripcion;
	private String imagen;
	private String miniatura;

	public BeanDetallesProducto() {
	}

	public BeanDetallesProducto(int idProducto, String categoria, int cantidadDisponible, int precio, String descripcion, String imagen, String miniatura) {
		this.idProducto = idProducto;
		this.categoria = categoria;
		this.cantidadDisponible = cantidadDisponible;
		this.precio = precio;
		this.descripcion = descripcion;
		this.imagen = imagen;
		this.miniatura = miniatura;
	}

	@Override public String toString() {
		return "BeanDetallesProducto{" + "idProducto=" + idProducto + ", categoria=" + categoria + ", cantidadDisponible=" + cantidadDisponible + ", precio=" + precio + ", descripcion=" + descripcion + ", imagen=" + imagen + ", miniatura=" + miniatura + '}';
	}

	@Override public int hashCode() {
		int hash = 5;
		hash = 71 * hash + this.idProducto;
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
		final BeanDetallesProducto other = (BeanDetallesProducto) obj;
		return this.idProducto == other.idProducto;
	}

	public int getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(int idProducto) {
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

	/**
	 * Procedimiento que crea la entidad a partir del objeto
	 * @param producto
	 * @return 
	 */
	public static ProductoEntity toEntity(BeanDetallesProducto producto) {
		final ProductoEntity entity = new ProductoEntity();
		entity.setIdProducto(String.valueOf(producto.getIdProducto()));
		entity.setCategoria(producto.getCategoria());
		entity.setDescripcion(producto.getDescripcion());
		entity.setCantidadDisponible(producto.cantidadDisponible);
		entity.setPrecio(producto.getPrecio());
		entity.setImagen(producto.getImagen());
		entity.setMiniatura(producto.getMiniatura());
		return entity;
	}
	
	/**
	 * Procedimiento que crea el objeto a partir de la entidadd
	 * @param entity
	 * @return 
	 */
	public static BeanDetallesProducto fromEntity(ProductoEntity entity) {
		final BeanDetallesProducto producto = new BeanDetallesProducto();
		producto.setIdProducto(Integer.parseInt(entity.getIdProducto()));
		producto.setCategoria(entity.getCategoria());
		producto.setDescripcion(entity.getDescripcion());
		producto.setCantidadDisponible(entity.getCantidadDisponible());
		producto.setPrecio(entity.getPrecio());
		producto.setImagen(entity.getImagen());
		producto.setMiniatura(entity.getMiniatura());
		return producto;
	}

}
