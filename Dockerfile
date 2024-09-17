<<<<<<< HEAD
# Sử dụng Maven 3.9.8 với Amazon Corretto 17 để build
FROM maven:3.9.8-amazoncorretto-17 AS build

# Set thư mục làm việc trong container
WORKDIR /app

# Sao chép file POM và mã nguồn vào container
COPY pom.xml .
COPY src src

# Build ứng dụng bằng Maven
RUN mvn clean package -DskipTests

# Sử dụng Amazon Corretto 17 để chạy ứng dụng
FROM amazoncorretto:17

# Đặt thư mục tạm cho container
VOLUME /tmp

# Định nghĩa biến cho file JAR
ARG JAR_FILE=target/BE_PBL6_FastOrderSystem-0.0.1-SNAPSHOT.jar

# Sao chép file JAR từ container build sang container runtime
COPY --from=build /app/${JAR_FILE} app.jar

# Expose cổng 8080
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "/app.jar"]
=======
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
>>>>>>> 7b903cd8995a6bee3c5be38ef0e2db44f4bea023
