FROM openjdk:21

EXPOSE 8080

ADD target/backend-manager.jar backend-manager.jar

ENTRYPOINT ["java", "-jar", "/backend-manager.jar"]