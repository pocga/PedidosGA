package com.sophossolutions.pocga.redis.service;

import com.sophossolutions.pocga.api.exceptions.ErrorEntidadNoEncontrada;
import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.redis.entity.ProductoEntity;
import com.sophossolutions.pocga.redis.repository.ProductosRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Implementación de los servicios para los productos
 * @author Ricardo José Ramírez Blauvelt
 */
@Service
public class ServicioProductosImpl implements ServicioProductos {

	/** Log de eventos */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServicioProductosImpl.class);
	
	@Value("${pedidosga.urlApiProductos}")
	private String urlApiProductos;

	@Autowired
	private ProductosRepository repository;
	
	@Override public BeanDetallesProducto getProducto(int idProducto) {
		// Consulta la entidad
		BeanDetallesProducto producto;
		final Optional<ProductoEntity> optional = repository.findById(String.valueOf(idProducto));
		if(optional.isPresent()) {
			// Mapea
			producto = BeanDetallesProducto.fromEntity(optional.get());
			LOGGER.info("Producto encontrado en cache: {}", producto);
		} else {
			LOGGER.info("Producto {} no registrado en cache. Buscando en API remota", idProducto);
			producto = getProductoDesdeApi(idProducto);
			LOGGER.info("Producto encontrado en API remota: {}", producto);
			if(producto != null) {
				setProducto(producto);
			} else {
				LOGGER.error("El producto {} no existe en el catálogo", idProducto);
				throw new ErrorEntidadNoEncontrada("El producto {" + idProducto + "} no existe en el catálogo");
			}
		}

		// Entrega el producto (puede ser nulo si no existe)
		return producto;
	}

	@Override public void setProducto(BeanDetallesProducto producto) {
		// Mapea
		final ProductoEntity entity = BeanDetallesProducto.toEntity(producto);
		
		// Almacena
		repository.save(entity);
	}

	@Override public boolean isProductoEnCatalogo(int idProducto) {
		return getProducto(idProducto) != null;
	}

	@Override public void removeProducto(int idProducto) {
		repository.deleteById(String.valueOf(idProducto));
	}

	@Override public void clearCacheProductos() {
		repository.deleteAll();
	}

	@Override public List<BeanCantidadProducto> fromMapProductos(Map<Integer, Integer> productos) {
		final Set<BeanCantidadProducto> listaProductos = new TreeSet<>();
		productos.forEach((productoEnMapa, cantidadEnMapa) -> {
			final BeanCantidadProducto bcp = new BeanCantidadProducto();
			bcp.setCantidad(cantidadEnMapa);
			bcp.setProducto(getProducto(productoEnMapa));
			listaProductos.add(bcp);
		});
		return new ArrayList<>(listaProductos);
	}

	@Override public Map<Integer, Integer> toMapProductos(List<BeanCantidadProducto> productos) {
		// Carga los productos
		return productos.stream()
			.collect(
				Collectors.toMap(
					bcp -> bcp.getProducto().getIdProducto(),
					BeanCantidadProducto::getCantidad
				)
			)
		;
	}

	@Override public BeanCantidadProducto fromBeanProducto(BeanProducto producto) {
		final BeanCantidadProducto bcp = new BeanCantidadProducto();
		bcp.setCantidad(producto.getCantidad());
		bcp.setProducto(getProducto(producto.getIdProducto()));
		return bcp;
	}

	@Override public BeanDetallesProducto getProductoDesdeApi(int idProducto) {
		// Consulta los detalles del producto en la API
		LOGGER.info("URL de la API de Productos: " + urlApiProductos);
		final ResponseEntity<BeanDetallesProducto> response = new RestTemplateBuilder()
			.build()
			.getForEntity(
				UriComponentsBuilder.fromHttpUrl(urlApiProductos).buildAndExpand(idProducto).toUri(),
				BeanDetallesProducto.class
			);
		if (!response.getStatusCode().is2xxSuccessful()) {
			return null;
		}
		LOGGER.info("ConsumirCatalogoApi -> Producto encontrado en API remota: {}", response);

		// Control
		if (response.getBody() == null || response.getBody().getIdProducto() == 0) {
			return null;
		}

		// Entrega el producto
		return response.getBody();
	}

}
