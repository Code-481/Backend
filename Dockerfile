FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-alpine
COPY --from=build /build/target/server_start.jar /server_start.jar
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/server_start.jar"]
