FROM openjdk:21

EXPOSE 8080

ADD target/mahoot-images.jar mahoot-images.jar

ENTRYPOINT ["java", "-jar", "/mahoot-images.jar"]