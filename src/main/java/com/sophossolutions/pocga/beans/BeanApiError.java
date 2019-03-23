package com.sophossolutions.pocga.beans;

import java.util.Objects;

/**
 * Bean de respuesta cuando hay errores
 * @author Ricardo José Ramírez Blauvelt
 */
public class BeanApiError
{
    public String codigoRespuesta;
    public String descripcionRespuesta;
    public String linkDocumentacionError;
    public String detallesCausaError;

	public BeanApiError() {
	}

	public BeanApiError(String codigoRespuesta, String descripcionRespuesta, String linkDocumentacionError, String detallesCausaError) {
		this.codigoRespuesta = codigoRespuesta;
		this.descripcionRespuesta = descripcionRespuesta;
		this.linkDocumentacionError = linkDocumentacionError;
		this.detallesCausaError = detallesCausaError;
	}

	@Override public String toString() {
		return "ApiErrorRest{" + "codigoRespuesta=" + codigoRespuesta + ", descripcionRespuesta=" + descripcionRespuesta + ", linkDocumentacionError=" + linkDocumentacionError + ", detallesCausaError=" + detallesCausaError + '}';
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

}