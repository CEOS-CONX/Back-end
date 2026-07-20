FROM eclipse-temurin:21-jdk
WORKDIR /app

RUN groupadd -g 1000 appuser && useradd -u 1000 -g appuser -m appuser
COPY build/libs/*.jar app.jar
RUN chown appuser:appuser app.jar

USER appuser

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]