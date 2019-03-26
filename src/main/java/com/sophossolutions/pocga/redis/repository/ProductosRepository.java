package com.sophossolutions.pocga.redis.repository;

import com.sophossolutions.pocga.redis.entity.ProductoEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Crea el repositorio para los pedidos
 * @author Ricardo José Ramírez Blauvelt
 */
public interface ProductosRepository extends CrudRepository<ProductoEntity, String>{

}
