package com.sophossolutions.pocga.model;

import com.sophossolutions.pocga.beans.BeanPedido;

/**
 * Servicio para enviar correos
 * @author Ricardo José Ramírez Blauvelt
 */
public interface ServicioCorreo {

	/**
	 * Procedimiento que envía el correo de confirmación del pedido
	 * @param pedido 
	 */
	void enviarConfirmacionPedido(BeanPedido pedido);

	/**
	 * Procedimiento que envía el correo de cancelación del pedido
	 * @param pedido 
	 */
	void enviarCancelacionPedido(BeanPedido pedido);

}
