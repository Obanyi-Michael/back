# Use a base image with Maven and Java 17
FROM maven:3.9.6-eclipse-temurin-17

# Set working directory
WORKDIR /app

# Copy the full source code into the container
COPY . .

# Build the app inside the container
RUN mvn clean package -DskipTests

# Expose the app's port
EXPOSE 8080

# Run the app using the built JAR
CMD ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]
