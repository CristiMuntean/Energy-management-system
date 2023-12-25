FROM maven:latest AS builder

WORKDIR /app
COPY ./pom.xml ./pom.xml
RUN mvn -B dependency:resolve-plugins dependency:resolve

COPY src ./src
RUN mvn clean package

FROM openjdk:17-slim
WORKDIR /device_microservice
COPY --from=builder /app/target/device_microservice-0.0.1-SNAPSHOT.jar /device_microservice/device_microservice-0.0.1-SNAPSHOT.jar
EXPOSE 8082

CMD ["sh", "-c", "sleep 50 && java -jar /device_microservice/device_microservice-0.0.1-SNAPSHOT.jar"]
