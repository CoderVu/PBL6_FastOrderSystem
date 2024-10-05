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
#docker run --name mysql -e MYSQL_DATABASE=db -e MYSQL_ROOT_PASSWORD=123456789 -p 3306:3306 -v mysql-data:/var/lib/mysql --network dockervu -d mysql:8.0.38
#docker run --name springboot -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/db -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=123456789 --network dockervu -d vunguyen2901/pbl6-app:latest