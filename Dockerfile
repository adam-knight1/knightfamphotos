# Use JDK 17 base image
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Optional: allow JAR version override at build time
ARG JAR_FILE=build/libs/knightfam-0.0.1-SNAPSHOT.jar

# Copy the built JAR into the container
COPY ${JAR_FILE} app.jar

# Set up health check for ALB/ECS
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
