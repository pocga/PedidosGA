package com.sophossolutions.pocga.cassandra;

import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Configura los repositorios de Cassandra
 * @author Ricardo José Ramírez Blauvelt
 */
@EnableCassandraRepositories(basePackages = "com.sophossolutions.pocga.cassandra.repository")
public class CassandraConfiguration {
	
}
