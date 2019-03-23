package com.sophossolutions.pocga.beans;

import java.util.Objects;

/**
 * Bean para los detalles del envío
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanDetallesEnvio {
	
	private String nombres;
	private String apellidos;
	private String direccion;
	private String ciudad;

	public BeanDetallesEnvio() {
	}

	public BeanDetallesEnvio(String nombre, String apellido, String direccion, String ciudad) {
		this.nombres = nombre;
		this.apellidos = apellido;
		this.direccion = direccion;
		this.ciudad = ciudad;
	}

	@Override public String toString() {
		return "BeanDetallesEnvio{" + "nombre=" + nombres + ", apellido=" + apellidos + ", direccion=" + direccion + ", ciudad=" + ciudad + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.nombres);
		hash = 89 * hash + Objects.hashCode(this.apellidos);
		hash = 89 * hash + Objects.hashCode(this.direccion);
		hash = 89 * hash + Objects.hashCode(this.ciudad);
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
		final BeanDetallesEnvio other = (BeanDetallesEnvio) obj;
		if (!Objects.equals(this.nombres, other.nombres)) {
			return false;
		}
		if (!Objects.equals(this.apellidos, other.apellidos)) {
			return false;
		}
		if (!Objects.equals(this.direccion, other.direccion)) {
			return false;
		}
		return Objects.equals(this.ciudad, other.ciudad);
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

}
