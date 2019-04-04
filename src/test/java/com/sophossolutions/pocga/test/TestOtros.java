package com.sophossolutions.pocga.test;

import com.datastax.driver.core.utils.UUIDs;
import com.sophossolutions.pocga.beans.BeanApiError;
import com.sophossolutions.pocga.beans.BeanCantidad;
import com.sophossolutions.pocga.beans.BeanCantidadProducto;
import com.sophossolutions.pocga.beans.BeanCrearPedido;
import com.sophossolutions.pocga.beans.BeanDetallesCarrito;
import com.sophossolutions.pocga.beans.BeanDetallesProducto;
import com.sophossolutions.pocga.beans.BeanPedido;
import com.sophossolutions.pocga.beans.BeanProducto;
import com.sophossolutions.pocga.beans.BeanTotales;
import com.sophossolutions.pocga.beans.BeanUsuario;
import com.sophossolutions.pocga.cassandra.entity.PedidosEntity;
import com.sophossolutions.pocga.redis.entity.CarritoEntity;
import com.sophossolutions.pocga.redis.entity.ProductoEntity;
import com.sophossolutions.pocga.redis.service.ServicioProductos;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests no relacionados con la API
 * @author Ricardo José Ramírez Blauvelt
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestOtros {
	
	@Autowired
	private ServicioProductos servicioProductos;
	
	private static final UUID ID_PEDIDO = UUIDs.timeBased();
	private static final String ID_USUARIO = "4fc8d378-e2aa-49ff-8f95-2465bcc68d25";
	
	
	@Test
	public void testBeanApiError() {
		// Bean 1
		final BeanApiError b1 = new BeanApiError();
		b1.setCodigoRespuesta(HttpStatus.OK.toString());
		b1.setDescripcionRespuesta("Descripción");
		b1.setLinkDocumentacionError("");
		b1.setDetallesCausaError("Detalles");
		
		// Bean 2
		final BeanApiError b2 = new BeanApiError(
			HttpStatus.OK.toString(), 
			"Descripción", 
			"", 
			"Detalles"
		);
		
		// Compara
		validar(b1, b2);
	}

	@Test
	public void testBeanCantidad() {
		// Bean 1
		final BeanCantidad b1 = new BeanCantidad();
		b1.setCantidad(5);
		
		// Bean 2
		final BeanCantidad b2 = new BeanCantidad(5);
		
		// Compara
		validar(b1, b2);
	}

	@Test
	public void testBeanCantidadProducto() {
		// Producto
		final BeanDetallesProducto producto = servicioProductos.getProducto(1);

		// Bean 1
		final BeanCantidadProducto b1 = new BeanCantidadProducto();
		b1.setProducto(producto);
		b1.setCantidad(5);
		
		// Bean 2
		final BeanCantidadProducto b2 = new BeanCantidadProducto(
			producto, 
			5
		);
		
		// Compara
		validar(b1, b2);
	}

	@Test
	public void testBeanCrearPedido() {
		// Producto
		final BeanProducto producto = new BeanProducto(1, 5);
		
		// Fecha
		final LocalDateTime fecha = LocalDateTime.now();

		// Bean 1
		final BeanCrearPedido b1 = new BeanCrearPedido();
		b1.setIdPedido(ID_PEDIDO);
		b1.setIdUsuario(ID_USUARIO);
		b1.setProductos(List.of(producto));
		b1.setNombreDestinatario("NombreDestinatario");
		b1.setDireccionDestinatario("DireccionDestinatario");
		b1.setCiudadDestinatario("CiudadDestinatario");
		b1.setTelefonoDestinatario("TelefonoDestinatario");
		b1.setFecha(fecha);
		
		// Bean 2
		final BeanCrearPedido b2 = new BeanCrearPedido(
			ID_PEDIDO, 
			ID_USUARIO, 
			List.of(producto), 
			"NombreDestinatario", 
			"DireccionDestinatario", 
			"CiudadDestinatario", 
			"TelefonoDestinatario", 
			fecha
		);
		
		// Compara
		validar(b1, b2);
	}

	@Test
	public void testBeanDetallesCarrito() {
		// Producto
		final BeanDetallesProducto producto = servicioProductos.getProducto(1);

		// Cantidad producto
		final BeanCantidadProducto cantidadProducto = new BeanCantidadProducto(producto, 5);

		// Usuario
		final BeanUsuario usuario = new BeanUsuario(ID_USUARIO);
		
		// Totales
		final BeanTotales totales = new BeanTotales(5, 0);

		// Bean 1
		final BeanDetallesCarrito b1 = new BeanDetallesCarrito();
		b1.setUsuario(usuario);
		b1.setProductos(List.of(cantidadProducto));
		b1.setTotales(totales);
		
		// Bean 2
		final BeanDetallesCarrito b2 = new BeanDetallesCarrito(
			usuario,
			List.of(cantidadProducto), 
			totales
		);
		
		// Compara
		validar(b1, b2);
	}

	@Test
	public void testBeanDetallesProducto() {
		// Bean 1
		final BeanDetallesProducto b1 = new BeanDetallesProducto();
		b1.setIdProducto(1);
		b1.setCategoria("TVs");
		b1.setCantidadDisponible(0);
		b1.setPrecio(1000);
		b1.setDescripcion("Descripción");
		b1.setImagen("URL de imagen");
		b1.setMiniatura("URL de la miniatura");

		// Bean 2
		final BeanDetallesProducto b2 = new BeanDetallesProducto(
			1,
			"TVs",
			0,
			1000,
			"Descripción",
			"URL de imagen",
			"URL de la miniatura"
		);

		// Compara
		validar(b1, b2);
	}
	
	@Test
	public void testBeanPedido() {
		// Producto
		final BeanDetallesProducto producto = servicioProductos.getProducto(1);

		// Cantidad producto
		final BeanCantidadProducto cantidadProducto = new BeanCantidadProducto(producto, 5);

		// Usuario
		final BeanUsuario usuario = new BeanUsuario(ID_USUARIO);
		
		// Fecha
		final LocalDateTime fecha = LocalDateTime.now();
		
		// Bean 1
		final BeanPedido b1 = new BeanPedido();
		b1.setIdPedido(ID_PEDIDO);
		b1.setUsuario(usuario);
		b1.setProductos(List.of(cantidadProducto));
		b1.setNombreDestinatario("Nombre");
		b1.setDireccionDestinatario("Dirección");
		b1.setCiudadDestinatario("Ciudad");
		b1.setTelefonoDestinatario("Teléfono");
		b1.setFecha(fecha);

		// Bean 2
		final BeanPedido b2 = new BeanPedido(
			ID_PEDIDO,
			usuario,
			List.of(cantidadProducto),
			"Nombre",
			"Dirección",
			"Ciudad",
			"Teléfono", 
			fecha
		);

		// Compara
		validar(b1, b2);
	}
	
	@Test
	public void testBeanProducto() {
		// Bean 1
		final BeanProducto b1 = new BeanProducto();
		b1.setIdProducto(1);
		b1.setCantidad(5);

		// Bean 2
		final BeanProducto b2 = new BeanProducto(1, 5);

		// Compara
		validar(b1, b2);
	}

	@Test
	public void testBeanTotales() {
		// Bean 1
		final BeanTotales b1 = new BeanTotales();
		b1.setTotalCantidad(1);
		b1.setTotalPrecio(5000);

		// Bean 2
		final BeanTotales b2 = new BeanTotales(1, 5000);

		// Compara
		validar(b1, b2);
	}

	@Test
	public void testBeanUsuario() {
		// Bean 1
		final BeanUsuario b1 = new BeanUsuario();
		b1.setIdUsuario(ID_USUARIO);
		b1.setEmail("cuenta@dominio.com");
		b1.setNombres("Nombres");
		b1.setApellidos("Apellidos");

		// Bean 2
		final BeanUsuario b2 = new BeanUsuario(
			ID_USUARIO, 
			"cuenta@dominio.com", 
			"Nombres", 
			"Apellidos"
		);

		// Compara
		validar(b1, b2);
	}

	@Test
	public void testCarritoEntity() {
		// Bean 1
		final CarritoEntity b1 = new CarritoEntity();
		b1.setIdUsuario(ID_USUARIO);
		b1.setProductos(Map.of(1, 5));
		
		// Bean 2
		final CarritoEntity b2 = new CarritoEntity(
			ID_USUARIO,
			Map.of(1, 5)
		);
		
		// Compara
		validar(b1, b2);
	}

	@Test
	public void testProductoEntity() {
		// Bean 1
		final ProductoEntity b1 = new ProductoEntity();
		b1.setIdProducto("1");
		b1.setCategoria("TVs");
		b1.setCantidadDisponible(0);
		b1.setPrecio(1000);
		b1.setDescripcion("Descripción");
		b1.setImagen("URL de imagen");
		b1.setMiniatura("URL de la miniatura");
		
		// Bean 2
		final ProductoEntity b2 = new ProductoEntity();
		b2.setIdProducto("1");
		b2.setCategoria("TVs");
		b2.setCantidadDisponible(0);
		b2.setPrecio(1000);
		b2.setDescripcion("Descripción");
		b2.setImagen("URL de imagen");
		b2.setMiniatura("URL de la miniatura");
		
		// Compara
		validar(b1, b2);
	}
	
	@Test
	public void testPedidoEntity() {
		// Fecha
		final LocalDateTime fecha = LocalDateTime.now();
		
		// Bean 1
		final PedidosEntity b1 = new PedidosEntity();
		b1.setIdPedido(ID_PEDIDO);
		b1.setIdUsuario(ID_USUARIO);
		b1.setProductos(Map.of(1, 5));
		b1.setNombreDestinatario("Nombre");
		b1.setDireccionDestinatario("Dirección");
		b1.setCiudadDestinatario("Ciudad");
		b1.setTelefonoDestinatario("Teléfono");
		b1.setFecha(fecha);
		
		// Bean 2
		final PedidosEntity b2 = new PedidosEntity();
		b2.setIdPedido(ID_PEDIDO);
		b2.setIdUsuario(ID_USUARIO);
		b2.setProductos(Map.of(1, 5));
		b2.setNombreDestinatario("Nombre");
		b2.setDireccionDestinatario("Dirección");
		b2.setCiudadDestinatario("Ciudad");
		b2.setTelefonoDestinatario("Teléfono");
		b2.setFecha(fecha);
		
		// Compara
		validar(b1, b2);
	}

	/**
	 * Procedimiento que realiza la comparación de beans
	 * @param <T>
	 * @param b1
	 * @param b2 
	 */
	private <T> void validar(T b1, T b2) {
		final String nombreClase = b1.getClass().getSimpleName();
		Assert.assertEquals(nombreClase + " 'equals' no coincide", b1, b2);
		Assert.assertEquals(nombreClase + " 'hashCode' no coincide", b1.hashCode(), b2.hashCode());
		Assert.assertTrue(nombreClase + " 'toString' no coincide", b1.toString().startsWith(nombreClase + "{"));
	}

}
