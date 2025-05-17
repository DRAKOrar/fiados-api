FROM eclipse-temurin:21-jdk as builder

WORKDIR /app

# Copiar archivos
COPY . .

# Asignar permisos
RUN chmod +x gradlew

# Construir el proyecto (sin test por ahora)
RUN ./gradlew clean build -x test

# Segunda etapa: imagen final más liviana
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiar solo el JAR generado
COPY --from=builder /app/build/libs/*.jar app.jar

# Exponer el puerto (ajústalo si usas otro)
EXPOSE 8080

# Ejecutar el JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
