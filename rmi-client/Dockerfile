# Build stage
FROM maven:3.8.6-openjdk-11-slim AS build

WORKDIR /app

# Copy the interface module first
COPY rmi-interface ./rmi-interface/

# Build the interface module
WORKDIR /app/rmi-interface
RUN mvn clean install -DskipTests

# Copy and build rmi-client
WORKDIR /app
COPY rmi-client ./rmi-client/
WORKDIR /app/rmi-client
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=build /app/rmi-client/target/rmi-client-1.0.0.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
