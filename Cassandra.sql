-- Crea el keyspace
CREATE KEYSPACE pocga WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};
USE pocga;

-- Tabla para el carrito de compras
CREATE TABLE IF NOT EXISTS carrito (
    id_usuario TEXT PRIMARY KEY, 
    productos MAP<INT, INT>
) WITH comment = 'Detalles del carrito';

insert into carrito (id_usuario, productos) values ('ricardo', {1:1,2:2});
insert into carrito (id_usuario, productos) values ('ana', {2:1,3:1});

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


insert into pedidos (id_pedido, id_usuario, productos, nombre_destinatario, direccion_destinatario, ciudad_destinatario, telefono_destinatario, fecha) values (now(), 'ana', {1:1, 2:1}, 'Ana Muñoz', 'Yerbabuena', 'Medellín', '5798382', dateof(now()));
insert into pedidos (id_pedido, id_usuario, productos, nombre_destinatario, direccion_destinatario, ciudad_destinatario, telefono_destinatario, fecha) values (now(), 'ricardo', {3:1}, 'Ricardo Ramírez', 'Villa del Aburrá', 'Medellín', '2503378', dateof(now()));
