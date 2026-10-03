
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable JAR
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Minimal Java 21 Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy built artifact from the build stage
COPY --from=build /app/target/student-erp-mcp-server-1.0.0-SNAPSHOT.jar app.jar

# Expose port 8080 (used for SSE / HTTP transport)
EXPOSE 8080

# Run the Spring Boot JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
