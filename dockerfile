# Usa una imagen base con Java 21 y Gradle preinstalado
FROM eclipse-temurin:21-jdk as build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos del proyecto
COPY . .

# Da permisos al wrapper de Gradle
RUN chmod +x ./gradlew

# Construye la aplicación (sin tests ni chequeos para producción rápida)
RUN ./gradlew clean build -x check -x test

# Etapa final: usar una imagen más ligera para ejecutar
FROM eclipse-temurin:21-jdk as runtime

WORKDIR /app

# Copia el JAR construido
COPY --from=build /app/build/libs/*.jar app.jar

# Expone el puerto (ajusta si tu app usa otro)
EXPOSE 8080

# Comando de arranque
CMD ["java", "-jar", "app.jar"]
