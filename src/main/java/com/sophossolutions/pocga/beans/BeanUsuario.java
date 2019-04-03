package com.sophossolutions.pocga.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 * Bean para almacenar los datos de un usuario
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanUsuario implements Serializable {

	private String idUsuario;
	private String email;
	private String name;

	public BeanUsuario() {
	}

	public BeanUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
		this.email = idUsuario;
		this.name = idUsuario;
	}

	@Override public String toString() {
		return "BeanUsuario{" + "idUsuario=" + idUsuario + ", email=" + email + ", name=" + name + '}';
	}

	@Override public int hashCode() {
		int hash = 5;
		hash = 41 * hash + Objects.hashCode(this.idUsuario);
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
		final BeanUsuario other = (BeanUsuario) obj;
		return Objects.equals(this.idUsuario, other.idUsuario);
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
