FROM marceloagmelo/maven-3.6.3-openjdk-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:11
COPY --from=build /home/app/target/social-dialog-1.0-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
EXPOSE 8080

#FROM openjdk:17-alpine
#
#WORKDIR /app
#
#ADD ./src/main/resources/static .
#
#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
#RUN ./mvnw dependency:go-offline
#
#COPY src ./src
#
#CMD ["./mvnw", "spring-boot:run"]