package com.sophossolutions.pocga.redis.repository;

import com.sophossolutions.pocga.redis.entity.CarritoEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Crea el repositorio para el carrito
 * @author Ricardo José Ramírez Blauvelt
 */
public interface CarritoRepository extends CrudRepository<CarritoEntity, String> {
	
}
