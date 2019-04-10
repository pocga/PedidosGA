package com.sophossolutions.pocga.config;

import com.sophossolutions.pocga.utils.AES;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Configuración del correo electrónico
 * @author Ricardo José Ramírez Blauvelt
 */
@Configuration
public class MailConfig {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MailConfig.class);
	
	@Value(("${spring.mail.host}"))
	private String host;
	
	@Value(("${spring.mail.username}"))
	private String userName;
	
	@Value(("${spring.mail.password}"))
	private String userPassword;
	
	@Value(("${spring.mail.mime.charset}"))
	private String charset;
	
	@Value(("${spring.mail.properties.mail.transport.protocol}"))
	private String protocol;
	
	@Value(("${spring.mail.properties.mail.smtp.port}"))
	private int port;
	
	@Value(("${spring.mail.properties.mail.smtp.auth}"))
	private boolean auth;
	
	@Value(("${spring.mail.properties.mail.smtp.starttls.enable}"))
	private boolean enableTLS;
	
	@Value(("${spring.mail.properties.mail.smtp.starttls.required}"))
	private boolean requiredTLS;


	@Bean
	public JavaMailSender getJavaMailSender() {
		// Crea el sender
		final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		// Propiedades de conexión
		final Properties mailProperties = new Properties();
		mailProperties.put("mail.transport.protocol", protocol);
		mailProperties.put("mail.smtp.port", port);
		mailProperties.put("mail.smtp.auth", auth);
		mailProperties.put("mail.smtp.starttls.enable", enableTLS);
		mailProperties.put("mail.smtp.starttls.required", requiredTLS);

		// Configura el sender
		mailSender.setJavaMailProperties(mailProperties);
		mailSender.setDefaultEncoding(charset);
		mailSender.setHost(host);
		mailSender.setPort(port);
		mailSender.setProtocol(protocol);
		try {
			mailSender.setUsername(AES.descifrar(userName));
			mailSender.setPassword(AES.descifrar(userPassword));
		} catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			LOGGER.error("Error descifrando los datos de conexión al servidor SMTP. Excepción: {} -> {}", e.getClass().getSimpleName(), e);
			return null;
		}

		// Entrega el sender
		return mailSender;
	}

}
