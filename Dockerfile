# Use an official Maven image with JDK 17 as a parent image
FROM maven:3.8.4-openjdk-17-slim AS builder

# Set the working directory in the container
WORKDIR /app

# Copy the entire project to the container
COPY . .

# Build the application using the Maven Wrapper
RUN mvn clean package

# Create a new image for running the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file built in the previous stage
COPY --from=builder /app/target/instalite-0.0.1-SNAPSHOT.jar .

# Expose the port that your Spring Boot app will run on
EXPOSE 8080

# Specify the command to run your application
CMD ["java", "-jar", "instalite-0.0.1-SNAPSHOT.jar"]
