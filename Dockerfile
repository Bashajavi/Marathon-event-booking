# Build stage
FROM maven:3.9-amazoncorretto-17-al2023 AS build
WORKDIR /app
COPY . .

# Use the efficient Maven build process with caching
RUN mvn clean install -DskipTests

# Package stage with a smaller image (Alpine)
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/springsecurity-learning-bcrypt-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the default port
EXPOSE 8080

# JVM optimization for high performance and memory handling
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:+UseG1GC", "-Xmx2048m", "-Xms1024m", "-Dspring.jpa.open-in-view=false", "-jar", "/app/app.jar"]
