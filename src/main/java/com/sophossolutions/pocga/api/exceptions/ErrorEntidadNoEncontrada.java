package com.sophossolutions.pocga.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para una entidad no encontrada
 * @author Ricardo José Ramírez Blauvelt
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ErrorEntidadNoEncontrada extends RuntimeException {
	
	public ErrorEntidadNoEncontrada(String mensaje) {
		super(mensaje);
	}

}
