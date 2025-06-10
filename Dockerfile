# Stage 1: Build the application using a Maven image
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Package the application, skipping tests. This command also downloads all dependencies.
RUN mvn package -DskipTests

# Stage 2: Create the final, smaller image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the generated JAR file from the build stage
# The JAR name is based on the artifactId and version in your pom.xml
COPY --from=build /app/target/synonym-network-api-1.0-SNAPSHOT.jar app.jar

# Expose the port that your application runs on
EXPOSE 8080

# The command to run your application
ENTRYPOINT ["java","-jar","app.jar"]
