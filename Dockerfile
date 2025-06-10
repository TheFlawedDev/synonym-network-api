# Stage 1: Build the application using a Maven image
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Package the application, skipping tests. This creates the JAR file.
RUN mvn package -DskipTests


# Stage 2: Create the final, smaller image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port that your application runs on (Spring Boot default is 8080)
EXPOSE 8080

# The command to run your application
ENTRYPOINT ["java","-jar","app.jar"]
