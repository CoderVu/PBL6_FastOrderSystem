# Build stage
FROM maven:3.9.9-amazoncorretto-17 AS build
# Set the working directory
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Package stage
FROM amazoncorretto:17
# Set the working directory
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
#docker run -d -p 3307:3306 --name mysql-container -e MYSQL_ROOT_PASSWORD=123456789 mysql:8.0.38
# docker run --name systemfastorder --network dockervu -p 8080:8080 -e DBMS_CONNECTION=jdbc:mysql://mysql:3306/db_docker_pbl6 fastordersystem:0.0.1 .