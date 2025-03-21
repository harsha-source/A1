# Use an official OpenJDK 17 image from Docker Hub
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/A1-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
