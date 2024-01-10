# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS builder

# Set the working directory
WORKDIR /app

# First, copy the pom.xml file. This allows Docker to cache the Maven dependencies
COPY pom.xml .

# Copy the project source
COPY src src

# Build the application using Maven
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Create a user and group with non-root privileges
RUN groupadd -r spring && useradd -r -g spring spring

# Set the working directory
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Specify the user to run the application
USER spring:spring

# Expose the application's port
EXPOSE 8080

# Health check (customize the endpoint accordingly)
HEALTHCHECK --interval=30s --timeout=30s --retries=5 CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
CMD ["java", "-jar", "app.jar"]
