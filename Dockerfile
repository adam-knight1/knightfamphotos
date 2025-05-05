# RUN ls -R for debugging when needed

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/knightfam-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

