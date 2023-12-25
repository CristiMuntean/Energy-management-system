FROM maven:latest AS builder

WORKDIR /app
COPY ./pom.xml ./pom.xml
RUN mvn -B dependency:resolve-plugins dependency:resolve

COPY src ./src
RUN mvn clean package

FROM openjdk:17-slim
WORKDIR /chat_microservice
COPY --from=builder /app/target/chat_microservice-0.0.1-SNAPSHOT.jar /chat_microservice/chat_microservice-0.0.1-SNAPSHOT.jar
EXPOSE 8082

CMD ["sh", "-c", "sleep 50 && java -jar /chat_microservice/chat_microservice-0.0.1-SNAPSHOT.jar"]
