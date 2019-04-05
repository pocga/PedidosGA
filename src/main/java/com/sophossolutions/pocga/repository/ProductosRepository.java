package com.sophossolutions.pocga.repository;

import com.sophossolutions.pocga.entity.ProductoEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Crea el repositorio para los productos
 * @author Ricardo José Ramírez Blauvelt
 */
public interface ProductosRepository extends CrudRepository<ProductoEntity, String> {

}
