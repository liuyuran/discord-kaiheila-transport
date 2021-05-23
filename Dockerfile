FROM maven:3.8.1-adoptopenjdk-11
RUN mvn package
ARG JAR_FILE=message-transport-server/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
