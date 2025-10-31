# --- Etapa 1: Construir la aplicación Spring Boot ---
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY TarjetaEstilosSpring /app/TarjetaEstilosSpring
WORKDIR /app/TarjetaEstilosSpring
RUN mvn clean package -DskipTests

# --- Etapa 2: Ejecución ---
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copia el JAR de la etapa de construcción
COPY --from=build /app/TarjetaEstilosSpring/target/*.jar app.jar
# Comando de inicio
CMD ["java", "-jar", "/app/app.jar"]