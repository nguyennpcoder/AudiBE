# ---------- build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies first
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Only the sources are needed for the package step
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Render injects $PORT; MaxRAMPercentage keeps the JVM inside the 512MB instance
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -XX:TieredStopAtLevel=1 -Djava.security.egd=file:/dev/./urandom" \
    PORT=8088

COPY --from=build /app/target/audi-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8088

# exec form through sh so $JAVA_OPTS / $PORT are expanded by the shell
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
