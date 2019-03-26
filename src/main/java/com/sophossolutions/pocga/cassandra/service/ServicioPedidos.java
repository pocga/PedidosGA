package com.sophossolutions.pocga.cassandra.service;

import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.cassandra.entity.PedidosEntity;
import java.util.List;
import java.util.UUID;

/**
 * Servicios ofrecidos para Pedidos
 * @author Ricardo José Ramírez Blauvelt
 */
public interface ServicioPedidos {

	/**
	 * Servicio que devuelve todos los pedidos registrados en el sistema
	 * @return 
	 */
	List<BeanPedido> getPedidos();
	
	/**
	 * Servicio que devuelve un pedido
	 * @param idPedido
	 * @return 
	 */
	BeanPedido getPedido(UUID idPedido);
	
	/**
	 * Servicio que devuelve todos los pedidos del usuario
	 * @param idUsuario
	 * @return 
	 */
	List<BeanPedido> getPedidos(String idUsuario);
	
	/**
	 * Procedimiento que crea una pedido
	 * @param pedido
	 * @return 
	 */
	BeanPedido crearPedido(BeanPedido pedido);
	
	/**
	 * Procedimiento que elimina un pedido
	 * @param idPedido 
	 */
	void eliminarPedido(UUID idPedido);

	/**
	 * Procedimiento que genera un objeto a partir de la entidad
	 * @param entity
	 * @return 
	 */
	BeanPedido fromEntity(PedidosEntity entity);

}
