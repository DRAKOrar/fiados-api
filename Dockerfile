# Etapa 1: Construcción con Gradle
FROM gradle:7.6.1-jdk17 AS build
COPY --chown=gradle:gradle src/main/java/com/fiados_api /app
WORKDIR /app
RUN gradle build --no-daemon

# Etapa 2: Imagen de ejecución con JDK
FROM openjdk:17-jdk-slim
EXPOSE 8080
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
