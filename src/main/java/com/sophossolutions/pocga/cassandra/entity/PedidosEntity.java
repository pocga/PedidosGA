package com.sophossolutions.pocga.cassandra.entity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Entidad de los pedidos
 * @author Ricardo José Ramírez Blauvelt
 */
@Table(value = "pedidos")
public class PedidosEntity {
	
	@PrimaryKey(value = "id_pedido")
	private UUID idPedido;

	@Column(value = "id_usuario")
	private String idUsuario;

	@Column(value = "productos")
	private Map<Integer, Integer> productos;

	@Column(value = "nombre_destinatario")
	private String nombreDestinatario;

	@Column(value = "direccion_destinatario")
	private String direccionDestinatario;

	@Column(value = "ciudad_destinatario")
	private String ciudadDestinatario;

	@Column(value = "telefono_destinatario")
	private String telefonoDestinatario;

	@Column(value = "fecha")
	private LocalDateTime fecha;

	@Override public String toString() {
		return "PedidosEntity{" + "idPedido=" + idPedido + ", idUsuario=" + idUsuario + ", productos=" + productos + ", nombreDestinatario=" + nombreDestinatario + ", direccionDestinatario=" + direccionDestinatario + ", ciudadDestinatario=" + ciudadDestinatario + ", telefonoDestinatario=" + telefonoDestinatario + ", fecha=" + fecha + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.idPedido);
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
		final PedidosEntity other = (PedidosEntity) obj;
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

	public Map<Integer, Integer> getProductos() {
		return productos;
	}

	public void setProductos(Map<Integer, Integer> productos) {
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

}
