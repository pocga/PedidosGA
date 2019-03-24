package com.sophossolutions.pocga.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para un listado de entidades vacío
 * @author Ricardo José Ramírez Blauvelt
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ErrorListadoEntidadesVacio extends RuntimeException {
	
	public ErrorListadoEntidadesVacio(String mensaje) {
		super(mensaje);
	}

}
