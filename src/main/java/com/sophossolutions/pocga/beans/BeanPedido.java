package com.sophossolutions.pocga.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sophossolutions.pocga.cassandra.entity.PedidosEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Bean con los detalles del pedido
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanPedido implements Comparable<BeanPedido> {
	
	private UUID idPedido;
	private String idUsuario;
	private List<BeanCantidadProducto> productos;
	private String nombreDestinatario;
	private String direccionDestinatario;
	private String ciudadDestinatario;
	private String telefonoDestinatario;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime fecha;

	public BeanPedido() {
	}

	public BeanPedido(UUID idPedido, String idUsuario, List<BeanCantidadProducto> productos, String nombreDestinatario, String direccionDestinatario, String ciudadDestinatario, String telefonoDestinatario, LocalDateTime fecha) {
		this.idPedido = idPedido;
		this.idUsuario = idUsuario;
		this.productos = productos;
		this.nombreDestinatario = nombreDestinatario;
		this.direccionDestinatario = direccionDestinatario;
		this.ciudadDestinatario = ciudadDestinatario;
		this.telefonoDestinatario = telefonoDestinatario;
		this.fecha = fecha;
	}

	@Override public String toString() {
		return "BeanPedido{" + "idPedido=" + idPedido + ", idUsuario=" + idUsuario + ", productos=" + productos + ", nombreDestinatario=" + nombreDestinatario + ", direccionDestinatario=" + direccionDestinatario + ", ciudadDestinatario=" + ciudadDestinatario + ", telefonoDestinatario=" + telefonoDestinatario + ", fecha=" + fecha + '}';
	}

	@Override public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(this.idPedido);
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
		final BeanPedido other = (BeanPedido) obj;
		return Objects.equals(this.idPedido, other.idPedido);
	}

	public UUID getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(UUID idPedido) {
		this.idPedido = idPedido;
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

	public String getNombreDestinatario() {
		return nombreDestinatario;
	}

	public void setNombreDestinatario(String nombreDestinatario) {
		this.nombreDestinatario = nombreDestinatario;
	}

	public String getDireccionDestinatario() {
		return direccionDestinatario;
	}

	public void setDireccionDestinatario(String direccionDestinatario) {
		this.direccionDestinatario = direccionDestinatario;
	}

	public String getCiudadDestinatario() {
		return ciudadDestinatario;
	}

	public void setCiudadDestinatario(String ciudadDestinatario) {
		this.ciudadDestinatario = ciudadDestinatario;
	}

	public String getTelefonoDestinatario() {
		return telefonoDestinatario;
	}

	public void setTelefonoDestinatario(String telefonoDestinatario) {
		this.telefonoDestinatario = telefonoDestinatario;
	}
	
	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	@Override public int compareTo(BeanPedido o) {
		return this.getIdPedido().compareTo(o.getIdPedido());
	}

	/**
	 * Procedimiento que crea el bean, a partir de la entidad correspondiente
	 * @param entity
	 * @return 
	 */
	public static BeanPedido fromEntity(PedidosEntity entity) {
		// Crea la entidad
		final BeanPedido pedido = new BeanPedido();
		pedido.setIdPedido(entity.getIdPedido());
		pedido.setIdUsuario(entity.getIdUsuario());
		pedido.setProductos(BeanCantidadProducto.fromMapProductos(entity.getProductos()));
		pedido.setNombreDestinatario(entity.getNombreDestinatario());
		pedido.setDireccionDestinatario(entity.getDireccionDestinatario());
		pedido.setCiudadDestinatario(entity.getCiudadDestinatario());
		pedido.setTelefonoDestinatario(entity.getTelefonoDestinatario());
		pedido.setFecha(entity.getFecha());

		// La entrega
		return pedido;
	}
}
