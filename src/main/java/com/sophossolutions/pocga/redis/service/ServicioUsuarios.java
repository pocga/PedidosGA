package com.sophossolutions.pocga.redis.service;

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
import com.sophossolutions.pocga.beans.BeanUsuario;
import com.sophossolutions.pocga.utils.AES;
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
public class ServicioUsuarios {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicioUsuarios.class);
	
	@Value("${spring.profiles.active}")
	private String activeProfile;

	@Value("${pedidosga.awsCognitoGroupId}")
	private String awsCognitoGroupId;

	@Value("${pedidosga.awsAccessKey}")
	private String awsAccessKey;

	@Value("${pedidosga.awsSecretKey}")
	private String awsSecretKey;
	
	
	@Cacheable(cacheNames = "usuarios", key = "#idUsuario")
	public BeanUsuario getUserByIdUsuario(String idUsuario) {
		// Crea el bean
		final BeanUsuario usuario = new BeanUsuario();
		usuario.setIdUsuario(idUsuario);

		// Control
		if(activeProfile.equals("dev")) {
			LOGGER.warn("Perfil de desarrollo. No se busca el usuario en AWS Cognito y se retorna el mismo ID");
			usuario.setEmail(idUsuario);
			usuario.setName(idUsuario);
			return usuario;
		}

		// Provee las credenciales
		AWSCredentialsProvider credentialsProvider;
		try {
			final AWSCredentials credentials = new BasicAWSCredentials(AES.descifrar(awsAccessKey), AES.descifrar(awsSecretKey));
			credentialsProvider = new AWSStaticCredentialsProvider(credentials);
		} catch (Exception e) {
			throw new InvalidParameterException("No fue posible descifrar los datos de acceso a AWS Cognito -> " + e.getLocalizedMessage());
		}

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
				usuario.setName(attribute.getValue());
			}
		});

		// Entrega el correo
		return usuario;
	}

}
