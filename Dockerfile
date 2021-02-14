FROM openjdk:11
EXPOSE 8080
ADD ./target/*.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar", "/app.jar"]