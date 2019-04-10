package com.sophossolutions.pocga.config;

import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Configura los repositorios de Cassandra
 * @author Ricardo José Ramírez Blauvelt
 */
@EnableCassandraRepositories(basePackages = "com.sophossolutions.pocga.repository")
public class CassandraConfig {

}
