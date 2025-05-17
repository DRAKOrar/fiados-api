# Etapa de construcción
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia todo el contenido del proyecto al contenedor
COPY . .

# Da permisos de ejecución a gradlew
RUN chmod +x gradlew

# Build del proyecto sin tests (para acelerar)
RUN ./gradlew clean build -x test -x check --stacktrace

# Etapa de ejecución
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia el archivo jar generado en la etapa build
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
