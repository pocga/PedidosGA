package com.sophossolutions.pocga.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para errores creando entidades
 * @author Ricardo José Ramírez Blauvelt
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ErrorActualizandoEntidad extends RuntimeException {
	
	public ErrorActualizandoEntidad(String mensaje) {
		super(mensaje);
	}

}
