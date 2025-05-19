# Etapa 1: Construcción con Gradle Wrapper y Java 21
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN ./gradlew build --no-daemon

# Etapa 2: Imagen de ejecución con JDK 21
FROM eclipse-temurin:21-jdk
EXPOSE 8080
COPY --from=build /app/build/libs/fiados-api-0.0.1-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
