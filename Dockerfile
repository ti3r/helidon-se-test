FROM maven:3.6-jdk-11 as build

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

FROM adoptopenjdk:11-jre-hotspot
COPY --from=build /code/target/libs/* /app/libs/
COPY --from=build /code/target/helidon-se-test.jar /app/app.jar
CMD ["java", "-cp", "/app/libs/", "-jar", "/app/app.jar"]
EXPOSE 80