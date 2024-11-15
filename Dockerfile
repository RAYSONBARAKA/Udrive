

FROM openjdk:22

ARG JAR_FILE=target/*.jar

COPY ./target/DriverApp-0.0.1-SNAPSHOT.jar DriverApp.jar
 
ENTRYPOINT ["java", "-jar", "DriverApp.jar"]

EXPOSE 5050
