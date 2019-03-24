package com.sophossolutions.pocga.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Bean de respuesta cuando hay errores
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanApiError {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private final LocalDateTime timestamp = LocalDateTime.now();
	private String codigoRespuesta;
    private String descripcionRespuesta;
    private String linkDocumentacionError;
    private String detallesCausaError;

	public BeanApiError() {
	}

	public BeanApiError(String codigoRespuesta, String descripcionRespuesta, String linkDocumentacionError, String detallesCausaError) {
		this.codigoRespuesta = codigoRespuesta;
		this.descripcionRespuesta = descripcionRespuesta;
		this.linkDocumentacionError = linkDocumentacionError;
		this.detallesCausaError = detallesCausaError;
	}

	@Override public String toString() {
		return "BeanApiError{" + "timestamp=" + timestamp + ", codigoRespuesta=" + codigoRespuesta + ", descripcionRespuesta=" + descripcionRespuesta + ", linkDocumentacionError=" + linkDocumentacionError + ", detallesCausaError=" + detallesCausaError + '}';
	}

	@Override public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.codigoRespuesta);
		hash = 89 * hash + Objects.hashCode(this.descripcionRespuesta);
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
		final BeanApiError other = (BeanApiError) obj;
		if (!Objects.equals(this.codigoRespuesta, other.codigoRespuesta)) {
			return false;
		}
		return Objects.equals(this.descripcionRespuesta, other.descripcionRespuesta);
	}

	public String getCodigoRespuesta() {
		return codigoRespuesta;
	}

	public void setCodigoRespuesta(String codigoRespuesta) {
		this.codigoRespuesta = codigoRespuesta;
	}

	public String getDescripcionRespuesta() {
		return descripcionRespuesta;
	}

	public void setDescripcionRespuesta(String descripcionRespuesta) {
		this.descripcionRespuesta = descripcionRespuesta;
	}

	public String getLinkDocumentacionError() {
		return linkDocumentacionError;
	}

	public void setLinkDocumentacionError(String linkDocumentacionError) {
		this.linkDocumentacionError = linkDocumentacionError;
	}

	public String getDetallesCausaError() {
		return detallesCausaError;
	}

	public void setDetallesCausaError(String detallesCausaError) {
		this.detallesCausaError = detallesCausaError;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

}