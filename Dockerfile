# Usar una imagen base de OpenJDK 17 (compatible con el JAR generado)
FROM openjdk:17-jdk-slim

# Crear el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR generado a la ruta del contenedor
COPY target/api-vert-1.0.0-SNAPSHOT-fat.jar /app/vertx-app.jar

# Exponer el puerto que usará tu aplicación
EXPOSE 8888

# Comando para ejecutar el JAR al iniciar el contenedor
ENTRYPOINT ["java", "-jar", "/app/vertx-app.jar"]
