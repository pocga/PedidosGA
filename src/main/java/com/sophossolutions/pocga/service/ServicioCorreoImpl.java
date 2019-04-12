package com.sophossolutions.pocga.service;

import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.beans.BeanUsuario;
import com.sophossolutions.pocga.exceptions.ErrorCreandoEntidad;
import com.sophossolutions.pocga.model.ServicioCorreo;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Clase que envía los correos de confirmación de pedidos
 * @author Ricardo José Ramírez Blauvelt
 */
@Service
public class ServicioCorreoImpl implements ServicioCorreo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicioCorreoImpl.class);

	@Value("${spring.profiles.active}")
	private String activeProfile;
	
	@Autowired
	private JavaMailSender sender;
	
	@Override public void enviarConfirmacionPedido(BeanPedido pedido) {
		enviarCorreo(
			pedido.getIdPedido(), 
			pedido.getUsuario(), 
			"Tu pedido en TechShopGA fue generado con éxito", 
			"Este correo es para confirmar la creación exitosa de tu pedido <b>" + pedido.getIdPedido() +"</b>", 
			"¡Muchas gracias!"
		);
	}

	@Override public void enviarCancelacionPedido(BeanPedido pedido) {
		enviarCorreo(
			pedido.getIdPedido(), 
			pedido.getUsuario(), 
			"Tu pedido en TechShopGA fue cancelado", 
			"Este correo es para informar la cancelación por parte del administrador de tu pedido <b>" + pedido.getIdPedido() + "</b>", 
			"Saludos."
		);
	}

	/**
	 * Envía el correo
	 * @param idPedido
	 * @param usuario
	 * @param asunto
	 * @param mensaje
	 * @param despedida 
	 */
	private void enviarCorreo(UUID idPedido, BeanUsuario usuario, String asunto, String mensaje, String despedida) {
		// Plantilla
		final String BR = "<br />";
		final String plantilla = ""
			+ "<html>"
				+ "<body>"
					+ "Hola %s,"
					+ BR
					+ BR
					+ "%s."
					+ BR
					+ BR
					+ "%s"
					+ BR
					+ BR
					+ "<img src='cid:id101' />"
				+ "</body>"
			+ "</html>"
		+ "";

		try {
			// Define el mensaje
			final MimeMessage message = sender.createMimeMessage();

			// Configura el mensaje
			final MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(new InternetAddress("pedidos@techshopga.com", "Pedidos TechShopGA"));
			if(!activeProfile.equals("dev")) {
				helper.setTo(new InternetAddress(usuario.getEmail(), (usuario.getNombres() + " " + usuario.getApellidos()).strip()));
			} else {
				helper.setTo(new InternetAddress("pocga@sophossolutions.com", "PoC GA"));
			}
			helper.setText(String.format(plantilla, usuario.getNombres(), mensaje, despedida), true);
			helper.setSubject(asunto);

			// Añade el logo
			final ClassPathResource file = new ClassPathResource("logo-techshopga.png");
			helper.addInline("id101", file);

			// Envía el correo
			sender.send(message);

		} catch (MailException | MessagingException | UnsupportedEncodingException e) {
			final String error = "Error enviando el correo de confirmación del pedido '" + idPedido + "'. Excepción: " + e.getClass().getSimpleName() + " -> " + e.getLocalizedMessage();
			LOGGER.error(error, e);
			final ErrorCreandoEntidad ece = new ErrorCreandoEntidad(error);
			ece.addSuppressed(e);
			throw ece;
		}
	}

}
