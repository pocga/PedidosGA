# Imagen base (JRE 11)
FROM openjdk:11-jre-slim

# Etiquetas
LABEL maintainer="pocga@sophossolutions.com"
LABEL version="1.0"
LABEL description="Contenedor para la API de Pedidos"

# Volumen para archivos de trabajo de Tomcat
VOLUME /tmp

# Puerto que se expone de la aplicación
EXPOSE 8080

# Ruta temporal
ARG RUTA_JAR=/tmp/ApiPedidosGA/

# Archivo bootJAR generado
COPY *.jar ${RUTA_JAR}

# Instala CURL por conveniencia para pruebas
RUN apt-get -y update && apt-get -y install curl

# Archivo que espera a que Cassandra esté arriba
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod a+x /wait-for-it.sh

# Desempaca la aplicación y la lleva a la ruta
RUN cd ${RUTA_JAR} && unzip *.jar && mkdir /app && mkdir /app/lib && cp BOOT-INF/lib/* /app/lib && cp META-INF/* /app/META-INF && cp -r BOOT-INF/classes/* /app

# Comando que inicializa la aplicación
ENTRYPOINT ["java","-cp","app:app/lib/*","com.sophossolutions.pocga.Application"]
