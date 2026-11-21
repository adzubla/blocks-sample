# syntax=docker/dockerfile:1
# One Dockerfile for every Spring Boot module. docker-compose.yml passes the module path:
#   MODULE=services/order-service
# Maven runs inside the build, so the host needs only Docker.

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
ARG MODULE
# The cache mount keeps ~/.m2 between builds. "locked" makes parallel builds take turns
# instead of writing to the same local repository at once.
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    mvn -B -q -pl "${MODULE}" -am package -DskipTests \
 && cp "${MODULE}"/target/*.jar /app.jar

FROM eclipse-temurin:17-jre
COPY --from=build /app.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
