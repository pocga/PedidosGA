package com.sophossolutions.pocga.model;

import com.sophossolutions.pocga.beans.BeanCrearPedido;
import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.entity.PedidosEntity;
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
	BeanPedido crearPedido(BeanCrearPedido pedido);
	
	/**
	 * Procedimiento que elimina un pedido
	 * @param idPedido 
	 */
	void eliminarPedido(UUID idPedido);
	
	/**
	 * Procedimiento que permite eliminar todos los pedidos de un usuario
	 * @param idUsuario 
	 */
	void eliminarPedidosUsuario(String idUsuario);
	
	/**
	 * Procedimiento que genera un objeto a partir de la entidad
	 * @param entity
	 * @return 
	 */
	BeanPedido fromEntity(PedidosEntity entity);
	
}
