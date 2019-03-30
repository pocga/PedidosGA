-- Crea el keyspace
CREATE KEYSPACE IF NOT EXISTS pocga WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};
USE pocga;

-- Tabla para el carrito de compras
CREATE TABLE IF NOT EXISTS carrito (
    id_usuario TEXT PRIMARY KEY, 
    productos MAP<INT, INT>
) WITH comment = 'Detalles del carrito';

-- Tabla de pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido TIMEUUID, 
    id_usuario TEXT, 
    productos MAP<INT, INT>, 
    fecha TIMESTAMP, 
    nombre_destinatario TEXT, 
    direccion_destinatario TEXT, 
    ciudad_destinatario TEXT,
    telefono_destinatario TEXT,
    PRIMARY KEY (id_pedido)
) WITH comment = 'Registro de pedidos';
CREATE INDEX IF NOT EXISTS index_id_usuario ON pedidos (id_usuario);
