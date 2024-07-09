FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/demo.jar /app/application.jar

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/testDb
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
