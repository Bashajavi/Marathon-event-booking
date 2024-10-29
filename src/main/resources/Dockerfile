#
# Build stage
#

FROM maven:3.9-amazoncorretto-17-al2023 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /target/springsecurity-learning-bcrypt-0.0.1-SNAPSHOT.jar demo.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","springsecurity-learning-bcrypt.jar"]
