package com.sophossolutions.pocga.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para errores creando entidades
 * @author Ricardo José Ramírez Blauvelt
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ErrorCreandoEntidad extends RuntimeException {
	
	public ErrorCreandoEntidad(String mensaje) {
		super(mensaje);
	}

}
