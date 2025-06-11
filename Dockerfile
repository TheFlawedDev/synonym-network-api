# Stage 1: Build the application using Maven
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY . .
# Make the Maven Wrapper executable and run the package command
RUN chmod +x ./mvnw && ./mvnw package -DskipTests

# Stage 2: Create the final, smaller image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# IMPORTANT: Set the PORT environment variable. Render will provide this.
# If Render doesn't provide it, we'll default to 10000.
ENV PORT 10000
EXPOSE 10000

# Copy the built JAR file from the 'build' stage
COPY --from=build /app/target/synonym-network-api-1.0-SNAPSHOT.jar app.jar

# Set the command to run the application
# We are including the memory setting here directly.
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
