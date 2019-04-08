package com.sophossolutions.pocga.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.sophossolutions.pocga.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.beans.BeanUsuario;
import com.sophossolutions.pocga.model.ServicioUsuarios;
import com.sophossolutions.pocga.utils.AES;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Servicios ofrecidos para los usuarios -> Traer información de Cognito
 * @author Ricardo José Ramírez Blauvelt
 */
@Service
public class ServicioUsuariosImpl implements ServicioUsuarios {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicioUsuariosImpl.class);
	
	@Value("${pedidosga.aws.cognitoGroupId}")
	private String awsCognitoGroupId;

	@Value("${pedidosga.aws.accessKey}")
	private String awsAccessKey;

	@Value("${pedidosga.aws.secretKey}")
	private String awsSecretKey;
	
	
	@Cacheable(cacheNames = "usuarios", key = "#idUsuario")
	@Override public BeanUsuario getUserByIdUsuario(String idUsuario) {
		try {
			// Crea el bean
			final BeanUsuario usuario = new BeanUsuario(idUsuario);
			if(true) {
				usuario.setEmail("eliana.aguilar@sophossolutions.com");
				return usuario;
			}

			// Provee las credenciales
			final AWSCredentialsProvider credentialsProvider = getAWSCredentialsProvider();

			// Crea el cliente
			final AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard()
				.withCredentials(credentialsProvider)
				.withRegion(Regions.US_EAST_1)
				.build()
			;

			// Genera la consulta del usuario dentro del pool
			final AdminGetUserRequest userRequest = new AdminGetUserRequest()
				.withUsername(idUsuario)
				.withUserPoolId(awsCognitoGroupId)
			;

			// Filtra los resultados para entregar el email
			final AdminGetUserResult userResult = cognitoClient.adminGetUser(userRequest);
			userResult.getUserAttributes().forEach(attribute -> {
				if(attribute.getName().equals("email")) {
					usuario.setEmail(attribute.getValue());
				}
				if(attribute.getName().equals("name")) {
					usuario.setNombres(attribute.getValue());
				}
				if(attribute.getName().equals("family_name")) {
					usuario.setApellidos(attribute.getValue());
				}
			});

			// Entrega el correo
			LOGGER.info("Consulta de usuario '{}' en AWS Cognito exitosa", idUsuario);
			return usuario;

		} catch (RuntimeException e) {
			final String error = "Error buscando datos en AWS Cognito -> " + e.getLocalizedMessage();
			LOGGER.error("Error obteniendo los datos de Cognito para el usuario '{}'. Error: {}", idUsuario, error);
			final ErrorEntidadNoEncontrada eene = new ErrorEntidadNoEncontrada(error);
			eene.addSuppressed(e);
			throw eene;
		}
	}
	
	/**
	 * Procedimiento que entrega el proveedor de credenciales de AWS
	 * @return 
	 */
	private AWSCredentialsProvider getAWSCredentialsProvider() {
		try {
			final AWSCredentials credentials = new BasicAWSCredentials(
				AES.descifrar(awsAccessKey), 
				AES.descifrar(awsSecretKey)
			);
			return new AWSStaticCredentialsProvider(credentials);
		} catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			throw new InvalidParameterException("No fue posible descifrar los datos de acceso a AWS Cognito -> " + e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
		}
	}

}
