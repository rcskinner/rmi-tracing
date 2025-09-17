# Build stage
FROM maven:3.8.6-openjdk-11-slim AS build

WORKDIR /app

# Copy the interface module first
COPY rmi-interface ./rmi-interface/

# Build the interface module
WORKDIR /app/rmi-interface
RUN mvn clean install -DskipTests

# Copy and build rmi-server
WORKDIR /app
COPY rmi-server ./rmi-server/
WORKDIR /app/rmi-server
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=build /app/rmi-server/target/rmi-server-1.0.0.jar app.jar

EXPOSE 8081 1099

CMD ["java", "-jar", "app.jar"]
