package com.sophossolutions.pocga.cassandra.repository;

import com.sophossolutions.pocga.cassandra.entity.CarritoEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Crea el repositorio para el carrito
 * @author Ricardo José Ramírez Blauvelt
 */
public interface CarritoRepository extends CrudRepository<CarritoEntity, String> {
	
}
