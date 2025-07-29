# Use Maven + Java 17 image
FROM maven:3.9.6-eclipse-temurin-17

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the app
RUN mvn clean package -DskipTests

# Expose app port
EXPOSE 8080

# Run the built JAR
CMD ["java", "-jar", "target/*.jar"]
