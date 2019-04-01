# Introducción

Proyecto en Spring Boot (Java 11), con Gradle y pruebas unitarias jUnit, para la PoC GA. Expone la API para los servicios de "Carrito" y "Pedidos"

## Dependencias

Maneja persistencia y cache con los siguientes servicios:

- [x] Cassandra
- [x] Redis

Consume un servicio REST que le provee la información de productos

- [x] API Catálogo

## Prerrequisitos del ejecutable

1. Java 11
1. Conexión a Internet (acceso a la API)
1. Cassandra corriendo en el  puerto 9042, con keyspace "pocga" y tabla "pedidos"
1. Redis corriendo en el puerto 6379

## Prerrequisitos para compilar

1. Java 11
1. Gradle 4+
1. Conexión a Internet (acceso a la API)
1. Cassandra corriendo en  localhost:9042, con keyspace "pocga" y tabla "pedidos"
1. Redis corriendo en localhost:6379

## ¿Cómo se utiliza?

### Como JAR

Es una programa Java Spring Boot stand-alone

```console
java -jar PedidosGA-[version].jar
```

### Como contenedor

Una vez creada la imagen

```console
docker run --name [nombre-contenedor] -p 8080:8080 -d pocga/pedidosga
```

> **Es indispensable que sean accesibles instancias de Cassandra y Redis para funcionar**