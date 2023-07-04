FROM openjdk:11-jdk
WORKDIR /usr/app
COPY /target/author_service-0.0.1-SNAPSHOT.jar ./author_service.jar
COPY . .
ENTRYPOINT ["java","-jar","author_service.jar"]
EXPOSE 7071