FROM eclipse-temurin:21-jre AS base
WORKDIR /app

FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM base
ENV JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS}"
COPY --from=build /src/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
