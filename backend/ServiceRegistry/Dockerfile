# Use OpenJDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the jar file from target to the container
COPY target/ServiceRegistry-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional for local testing)
EXPOSE 8761

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
