package com.sophossolutions.pocga.api.exceptions;

import com.sophossolutions.pocga.beans.BeanApiError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Control centralizado de excepciones
 * @author Ricardo José Ramírez Blauvelt
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ErrorEntidadNoEncontrada.class)
	public ResponseEntity<BeanApiError> handleEntidadNoEncontrada(ErrorEntidadNoEncontrada ex, WebRequest request) {
		final HttpStatus responseStatus = HttpStatus.NOT_FOUND;
		return ResponseEntity.status(responseStatus).body(crearObjetoError(ex, request, responseStatus));
	}
	
	@ExceptionHandler(ErrorListadoEntidadesVacio.class)
	public ResponseEntity<BeanApiError> handleListadoEntidadesVacio(ErrorListadoEntidadesVacio ex, WebRequest request) {
		final HttpStatus responseStatus = HttpStatus.NOT_FOUND;
		return ResponseEntity.status(responseStatus).body(crearObjetoError(ex, request, responseStatus));
	}
	
	@ExceptionHandler(ErrorCreandoEntidad.class)
	public ResponseEntity<BeanApiError> handleErrorCreandoEntidad(ErrorCreandoEntidad ex, WebRequest request) {
		final HttpStatus responseStatus = HttpStatus.UNPROCESSABLE_ENTITY;
		return ResponseEntity.status(responseStatus).body(crearObjetoError(ex, request, responseStatus));
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return ResponseEntity.status(status).body(crearObjetoError(ex, request, status));
	}

	/**
	 * Crea el objeto de error genérico
	 * @param ex
	 * @param request
	 * @return 
	 */
	private BeanApiError crearObjetoError(Throwable ex, WebRequest request, HttpStatus status) {
		final BeanApiError error = new BeanApiError();
		error.setCodigoRespuesta(status.toString());
		error.setDescripcionRespuesta(ex.getLocalizedMessage());
		error.setLinkDocumentacionError("");
		error.setDetallesCausaError("Excepción: " + ex.getClass().getSimpleName() + " - Error: " + ex.getLocalizedMessage()  + " - Recurso: " + request.getDescription(false));
		return error;
	}

}
