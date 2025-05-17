# Etapa de construcción
FROM eclipse-temurin:22-jdk AS build
WORKDIR /app

COPY . .

RUN chmod +x gradlew

# Hacemos build, sin correr tests
RUN ./gradlew clean build -x test -x check --stacktrace

# Etapa de ejecución
FROM eclipse-temurin:22-jdk
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
