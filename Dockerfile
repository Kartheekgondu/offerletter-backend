# Render: choose Docker as the runtime.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/offer-letter-api-1.0.0.jar app.jar
# Keep the JVM inside Render's 512 MB free-tier memory limit.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"
EXPOSE 9091
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-9091} -jar /app/app.jar"]
