# Use Maven with Java 20
FROM maven:3.9.4-eclipse-temurin-20 AS build
COPY . .
RUN mvn clean package -DskipTests

# Use Amazon Corretto 20
FROM amazoncorretto:20-alpine3.17
COPY --from=build /target/audi-0.0.1-SNAPSHOT.jar audi.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","audi.jar"]