package com.sophossolutions.pocga.beans;

import java.io.Serializable;

/**
 * Bean para almacenar los datos de un usuario
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanUsuario implements Serializable {

	private String idUsuario;
	private String email;
	private String name;

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
