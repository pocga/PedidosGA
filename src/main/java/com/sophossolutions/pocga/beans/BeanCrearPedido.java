package com.sophossolutions.pocga.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sophossolutions.pocga.redis.service.ServicioProductos;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Bean simplificado para la creación de pedidos
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanCrearPedido implements Comparable<BeanCrearPedido> {
	
	private UUID idPedido;
	private String idUsuario;
	private List<BeanProducto> productos;
	private String nombreDestinatario;
	private String direccionDestinatario;
	private String ciudadDestinatario;
	private String telefonoDestinatario;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime fecha;

	@Autowired
	private ServicioProductos servicioProductos;
	
	public BeanCrearPedido() {
	}

	public BeanCrearPedido(UUID idPedido, String idUsuario, List<BeanProducto> productos, String nombreDestinatario, String direccionDestinatario, String ciudadDestinatario, String telefonoDestinatario, LocalDateTime fecha) {
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
		return "BeanCrearPedido{" + "idPedido=" + idPedido + ", idUsuario=" + idUsuario + ", productos=" + productos + ", nombreDestinatario=" + nombreDestinatario + ", direccionDestinatario=" + direccionDestinatario + ", ciudadDestinatario=" + ciudadDestinatario + ", telefonoDestinatario=" + telefonoDestinatario + ", fecha=" + fecha + ", servicioProductos=" + servicioProductos + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.idPedido);
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
		final BeanCrearPedido other = (BeanCrearPedido) obj;
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

	public List<BeanProducto> getProductos() {
		return productos;
	}

	public void setProductos(List<BeanProducto> productos) {
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

	@Override public int compareTo(BeanCrearPedido o) {
		return this.idPedido.compareTo(o.getIdPedido());
	}

}
