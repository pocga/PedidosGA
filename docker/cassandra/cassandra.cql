CREATE KEYSPACE IF NOT EXISTS pocga WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};
USE pocga;
CREATE TABLE IF NOT EXISTS pedidos (id_pedido TIMEUUID PRIMARY KEY, id_usuario TEXT, productos MAP<INT, INT>, fecha TIMESTAMP, nombre_destinatario TEXT, direccion_destinatario TEXT, ciudad_destinatario TEXT, telefono_destinatario TEXT) WITH comment = 'Registro de pedidos';
CREATE INDEX IF NOT EXISTS index_id_usuario ON pedidos (id_usuario);
