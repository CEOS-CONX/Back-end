FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
RUN chown ubuntu:ubuntu app.jar
USER ubuntu
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]