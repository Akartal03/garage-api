FROM openjdk:11
LABEL maintainer="Abdullah Kartal <akartal03@hotmail.com>"
LABEL version="1.0"
LABEL description="Garage api"
COPY target/garage-api-0.0.1-SNAPSHOT.jar garage-api.jar
ENTRYPOINT ["java", "-jar", "/garage-api.jar"]