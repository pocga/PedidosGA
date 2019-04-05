package com.sophossolutions.pocga.repository;

import com.sophossolutions.pocga.entity.CarritoEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Crea el repositorio para el carrito
 * @author Ricardo José Ramírez Blauvelt
 */
public interface CarritoRepository extends CrudRepository<CarritoEntity, String> {
	
}
