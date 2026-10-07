# syntax=docker/dockerfile:1
### Stage 1: build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY reporting-domain/pom.xml reporting-domain/pom.xml
COPY reporting-domain/reporting-domain-core/pom.xml reporting-domain/reporting-domain-core/pom.xml
COPY reporting-domain/reporting-application-service/pom.xml reporting-domain/reporting-application-service/pom.xml
COPY reporting-application/pom.xml reporting-application/pom.xml
COPY reporting-infrastructure/pom.xml reporting-infrastructure/pom.xml
COPY reporting-messaging/pom.xml reporting-messaging/pom.xml
COPY reporting-container/pom.xml reporting-container/pom.xml
RUN --mount=type=cache,target=/root/.m2 mvn -B -q dependency:go-offline -DskipTests || true
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn -B -q clean package -DskipTests \
 && cp reporting-container/target/reporting-container-*.jar /app/app.jar

### Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app && mkdir -p /app/report-output && chown -R app:app /app
WORKDIR /app
COPY --from=build /app/app.jar app.jar
USER app
ENV TZ=UTC \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8021
VOLUME /app/report-output
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
  CMD wget -qO- http://localhost:8021/actuator/health/liveness || exit 1
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
