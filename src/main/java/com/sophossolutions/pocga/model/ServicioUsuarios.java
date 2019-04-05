package com.sophossolutions.pocga.model;

import com.sophossolutions.pocga.beans.BeanUsuario;

/**
 * Servicios para los usuarios
 * @author Ricardo José Ramírez Blauvelt
 */
public interface ServicioUsuarios {

	/**
	 * Procedimiento que se encarga de obtener la información de los usuarios desde AWS Cognito
	 * @param idUsuario
	 * @return 
	 */
	BeanUsuario getUserByIdUsuario(String idUsuario);	

}
