FROM amazoncorretto:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} application.jar

CMD apt-get update -y
CMD mvn install

ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/application.jar"]
