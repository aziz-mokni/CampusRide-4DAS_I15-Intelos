# Build stage: dependencies are cached in their own layer, so code changes don't re-download them
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src src
RUN mvn -q package -DskipTests

# Run stage: JRE only, non-root user
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --no-create-home spring
USER spring
COPY --from=build /app/target/reputation-service-*.jar app.jar
EXPOSE 8084
# Override in docker-compose: MONGODB_URI, EUREKA_URL
ENTRYPOINT ["java", "-jar", "app.jar"]
