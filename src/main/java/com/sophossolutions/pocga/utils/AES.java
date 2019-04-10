package com.sophossolutions.pocga.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Clase que permite cifrar y descifrar información a partir de una llave maestra.
 * @author Ricardo José Ramírez Blauvelt
 * @see https://proandroiddev.com/security-best-practices-symmetric-encryption-with-aes-in-java-7616beaaade9
 */
public class AES {

	private AES() {
		
	}
	
	/** Instancia del logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(AES.class);

	/** Algoritmo */
	private static final String ALGORITHM = "AES/GCM/NoPadding";

	/** Authentication tag */
	private static final int TAG_LENGTH_BIT = 128;

	/** Vector de inicialización GCM */
	private static final int IV_LENGTH_BYTE = 12;

	/** Generador de secuencias aleatorias  */
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	
	/** Instancia de la clave secreta */
	private static SecretKey key;

	/** Instancia del cifrador */
	private static Cipher cipher;
	

	/**
	 * Crea las instancias
	 */
	static {
		try {
			final Resource resource = new ClassPathResource("secretkey");
			final byte[] secret = Files.readAllBytes(resource.getFile().toPath());
			key = new SecretKeySpec(secret, "AES");
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			LOGGER.error("Error inicializando el cifrador", e);
		}
	}

	/**
	 * Procedimiento que cifra el texto indicado.Para descifrarlo se debe 
	 * utilizar la misma llave y el mismo algoritmo.
	 * @param textoNormal
	 * @return
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 */
	public static synchronized String cifrar(String textoNormal) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
		// Vector de inicialización
		final byte[] iv = new byte[IV_LENGTH_BYTE];
		SECURE_RANDOM.nextBytes(iv);

		// Cifra el texto
		cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
		final byte[] cipherText = cipher.doFinal(textoNormal.getBytes());

		// Genera el buffer
		ByteBuffer byteBuffer = ByteBuffer.allocate(1 + iv.length + cipherText.length);
		byteBuffer.put((byte) iv.length);
		byteBuffer.put(iv);
		byteBuffer.put(cipherText);

		// Lo entrega como texto
		return new String(Base64.getEncoder().encode(byteBuffer.array()));
	}

	/**
	 * Procedimiento que descifra el texto indicado.Dicho texto debió ser 
	 * cifrado por la misma llave y el mismo algoritmo.
	 * @param textoCifrado
	 * @return
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 */
	public static synchronized String descifrar(String textoCifrado) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
		// Obtiene el texto cifrado
		final ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.getDecoder().decode(textoCifrado));
		
		// Lo rellena
		int ivLength = byteBuffer.get();
		final byte[] iv = new byte[ivLength];
		byteBuffer.get(iv);
		final byte[] encrypted = new byte[byteBuffer.remaining()];
		byteBuffer.get(encrypted);

		// Descifra los datos
		cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
		byte[] decrypted = cipher.doFinal(encrypted);

		// Lo entrega como texto
		return new String(decrypted);
	}

}
