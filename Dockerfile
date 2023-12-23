FROM maven:3.8.4-openjdk-17 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

FROM openjdk:17
COPY --from=build /usr/src/app/target/instalite-0.0.1-SNAPSHOT.jar app.jar
VOLUME /opt/app/data
ENTRYPOINT ["java","-jar","/app.jar"]