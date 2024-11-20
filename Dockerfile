FROM adoptopenjdk/openjdk11:jdk-11.0.2.9-slim
LABEL maintainer="Abdullah Kartal <akartal03@hotmail.com>"
LABEL version="1.0"
LABEL description="Garage api"
EXPOSE 8091
COPY target/garage-api-0.0.1-SNAPSHOT.jar garage-api.jar
ENTRYPOINT ["java","-jar","/garage-api.jar"]