# =======================================================
# ETAPA 1: Construcción del JAR con Maven y Java 17
# =======================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el módulo monolito y compilamos el JAR
COPY monolito/pom.xml monolito/pom.xml
COPY monolito/src monolito/src
WORKDIR /app/monolito
RUN mvn clean package -DskipTests

# =======================================================
# ETAPA 2: Imagen ligera de ejecución (JRE Alpine)
# =======================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos solo el ejecutable compilado en la etapa anterior
COPY --from=build /app/monolito/target/*.jar app.jar

# Puerto
EXPOSE 8080

# Ejecutamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]