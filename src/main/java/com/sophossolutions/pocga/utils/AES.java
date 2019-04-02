package com.sophossolutions.pocga.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Clase que permite cifrar y descifrar información a partir de una llave maestra.
 * @author Ricardo José Ramírez Blauvelt
 */
public class AES {

	private AES() {
		
	}

	/** Instancia del logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AES.class);

	/** Elimina el salto de línea por defecto */
	private static final byte[] LINE_BREAK = {};

	/** Instancia de la clave secreta */
	private static SecretKey key;

	/** Instancia del cifrador */
	private static Cipher cipher;

	/** Instancia del codificador */
	private static Base64 coder;

	/**
	 * Crea las instancias
	 */
	static {
		try {
			final Resource resource = new ClassPathResource("secretkey");
			final byte[] secret = Files.readAllBytes(resource.getFile().toPath());
			key = new SecretKeySpec(secret, "AES");
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			coder = new Base64(32, LINE_BREAK, true);
		} catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			LOGGER.error("Error inicializando el cifrador", e);
		}
	}

	/**
	 * Procedimiento que cifra el texto indicado. Para descifrarlo se debe
	 * utilizar esta misma clase.
	 * @param textoNormal
	 * @return
	 * @throws Exception
	 */
	public static synchronized String cifrar(String textoNormal) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = cipher.doFinal(textoNormal.getBytes());
		return new String(coder.encode(cipherText));
	}

	/**
	 * Procedimiento que descifra el texto indicado. Dicho texto debió ser
	 * cifrado por esta misma clase.
	 * @param textoCifrado
	 * @return
	 * @throws Exception
	 */
	public static synchronized String descifrar(String textoCifrado) throws Exception {
		byte[] encypted = coder.decode(textoCifrado.getBytes());
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(encypted);
		return new String(decrypted);
	}

}
