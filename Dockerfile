#
# Build stage
#
FROM maven:3.9-amazoncorretto-17-al2023 AS build
WORKDIR /app

# Cache dependencies to speed up subsequent builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the application source code
COPY . .

# Build the application and skip tests for faster builds
RUN mvn clean package -DskipTests -Dspring.profiles.active=prod

#
# Package stage
#
FROM openjdk:17-jdk-slim

# Set a non-root user for better security
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

# Copy the built JAR file from the build stage
COPY --from=build /app/target/springsecurity-learning-bcrypt-0.0.1-SNAPSHOT.jar /app.jar

# Expose the default application port
EXPOSE 8080

# Set JVM options for better performance (latency optimization)
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:InitiatingHeapOccupancyPercent=45 -Djava.security.egd=file:/dev/./urandom"

# Run the application with optimized JVM settings
ENTRYPOINT ["java", "$JAVA_OPTS", "-jar", "/app.jar"]
