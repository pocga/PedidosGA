package com.sophossolutions.pocga.model;

import com.sophossolutions.pocga.beans.BeanUsuario;
import java.util.UUID;

/**
 * Servicio para enviar correos
 * @author Ricardo José Ramírez Blauvelt
 */
public interface ServicioCorreo {

	/**
	 * Procedimiento que envía el correo de confirmación del pedido
	 * @param idPedido 
	 * @param usuario 
	 */
	void enviarConfirmacionPedido(UUID idPedido, BeanUsuario usuario);

	/**
	 * Procedimiento que envía el correo de cancelación del pedido
	 * @param idPedido 
	 * @param usuario 
	 */
	void enviarCancelacionPedido(UUID idPedido, BeanUsuario usuario);

}
