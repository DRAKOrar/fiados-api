# Etapa de compilación
FROM eclipse-temurin:21-jdk AS build

# Directorio de trabajo
WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Permisos al wrapper de gradle
RUN chmod +x ./gradlew

# Construir el proyecto (opcionalmente omitiendo tests)
RUN ./gradlew clean build -x check -x test

# Etapa de ejecución (más ligera)
FROM eclipse-temurin:21-jdk AS runtime

WORKDIR /app

# Copia el JAR construido desde la etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Puerto expuesto
EXPOSE 8080

# Comando para ejecutar la app
CMD ["java", "-jar", "app.jar"]
