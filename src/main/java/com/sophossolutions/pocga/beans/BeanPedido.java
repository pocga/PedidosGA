package com.sophossolutions.pocga.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Bean con los detalles del pedido
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanPedido implements Comparable<BeanPedido>, Serializable {
	
	private UUID idPedido;
	private BeanUsuario usuario;
	private List<BeanCantidadProducto> productos;
	private String nombreDestinatario;
	private String direccionDestinatario;
	private String ciudadDestinatario;
	private String telefonoDestinatario;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime fecha;
	private BeanTotales totales;

	public BeanPedido() {
	}

	public BeanPedido(UUID idPedido, BeanUsuario usuario, List<BeanCantidadProducto> productos, String nombreDestinatario, String direccionDestinatario, String ciudadDestinatario, String telefonoDestinatario, LocalDateTime fecha, BeanTotales totales) {
		this.idPedido = idPedido;
		this.usuario = usuario;
		this.productos = productos;
		this.nombreDestinatario = nombreDestinatario;
		this.direccionDestinatario = direccionDestinatario;
		this.ciudadDestinatario = ciudadDestinatario;
		this.telefonoDestinatario = telefonoDestinatario;
		this.fecha = fecha;
		this.totales = totales;
	}

	@Override public String toString() {
		return "BeanPedido{" + "idPedido=" + idPedido + ", usuario=" + usuario + ", productos=" + productos + ", nombreDestinatario=" + nombreDestinatario + ", direccionDestinatario=" + direccionDestinatario + ", ciudadDestinatario=" + ciudadDestinatario + ", telefonoDestinatario=" + telefonoDestinatario + ", fecha=" + fecha + ", totales=" + totales + '}';
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

	public BeanTotales getTotales() {
		return totales;
	}

	public void setTotales(BeanTotales totales) {
		this.totales = totales;
	}

	@Override public int compareTo(BeanPedido o) {
		return this.getIdPedido().compareTo(o.getIdPedido());
	}

	/**
	 * Mapea el bean
	 * @return 
	 */
	public BeanCrearPedido toBeanCrearPedido() {
		final BeanCrearPedido crear = new BeanCrearPedido();
		crear.setIdPedido(idPedido);
		crear.setIdUsuario(usuario.getIdUsuario());
		crear.setProductos(BeanProducto.toListProductos(productos));
		crear.setNombreDestinatario(nombreDestinatario);
		crear.setDireccionDestinatario(direccionDestinatario);
		crear.setCiudadDestinatario(ciudadDestinatario);
		crear.setTelefonoDestinatario(telefonoDestinatario);
		crear.setFecha(fecha);
		return crear;
	}

}
