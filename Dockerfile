#FROM maven:3-jdk-11-openj9 as build
#
#WORKDIR /code
#COPY * /code/
#RUN mvn clean package


FROM azul/zulu-openjdk-centos:11
COPY target/helidon-se-test-jar-with-dependencies.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
EXPOSE 8080