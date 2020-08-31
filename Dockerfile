##This build image supports arm/v7 builds for raspberry pi
FROM maven:3.6.3-adoptopenjdk-11 as build

WORKDIR /code
# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .
RUN mvn package -DskipTests
# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
RUN mvn package -DskipTests

##This base image supports arm runs for raspberry pi
FROM adoptopenjdk:11-jre-hotspot-bionic
COPY --from=build /code/target/libs/* /app/libs/
COPY --from=build /code/target/helidon-se-test.jar /app/app.jar
CMD ["java", "-cp", "/app/libs/", "-jar", "/app/app.jar"]
EXPOSE 80