package com.sophossolutions.pocga.repository;

import com.sophossolutions.pocga.entity.PedidosEntity;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

/**
 * Crea el repositorio para los pedidos
 * @author Ricardo José Ramírez Blauvelt
 */
public interface PedidosRepository extends CrudRepository<PedidosEntity, UUID>{

	/**
	 * Consulta que trae todos los pedidos de un usuario
	 * @param idUsuario
	 * @return 
	 */
	Iterable<PedidosEntity> findAllByIdUsuario(String idUsuario);

}
