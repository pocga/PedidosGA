FROM cassandra

EXPOSE 9042

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod a+x docker-entrypoint.sh

COPY cassandra.cql docker-entrypoint-initdb.d/initial-seed.cql
