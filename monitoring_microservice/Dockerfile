FROM maven:latest AS builder

WORKDIR /app
COPY ./pom.xml ./pom.xml
RUN mvn -B dependency:resolve-plugins dependency:resolve

COPY src ./src
RUN mvn clean package

FROM openjdk:17-slim
WORKDIR /device_microservice
COPY --from=builder /app/target/monitoring_microservice-0.0.1-SNAPSHOT.jar /monitoring_microservice/monitoring_microservice-0.0.1-SNAPSHOT.jar
EXPOSE 8082

CMD ["sh", "-c", "sleep 50 && java -jar /monitoring_microservice/monitoring_microservice-0.0.1-SNAPSHOT.jar"]
