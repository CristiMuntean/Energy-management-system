FROM maven:latest AS builder

WORKDIR /app
COPY ./pom.xml ./pom.xml
RUN mvn -B dependency:resolve-plugins dependency:resolve

COPY src ./src
RUN mvn clean package

FROM openjdk:17-slim
WORKDIR /user_microservice
COPY --from=builder /app/target/user_microservice-0.0.1-SNAPSHOT.jar /user_microservice/user_microservice-0.0.1-SNAPSHOT.jar
EXPOSE 8081

CMD ["sh", "-c", "sleep 50 && java -jar /user_microservice/user_microservice-0.0.1-SNAPSHOT.jar"]