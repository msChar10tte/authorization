# Вместо openjdk:8-jdk-alpine используем openjdk:17-jdk-alpine
FROM openjdk:17-jdk-alpine

# Указываем рабочую директорию внутри контейнера
WORKDIR /app

# Добавляем собранный JAR-файл в образ
ADD target/authorization-0.0.1-SNAPSHOT.jar app.jar

# Открываем порт, на котором работает Spring Boot приложение (по умолчанию 8080)
EXPOSE 8080

# Команда для запуска приложения при старте контейнера
ENTRYPOINT ["java","-jar","app.jar"]