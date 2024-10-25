# Используйте официальный образ JDK
FROM openjdk:11-jre-slim

# Создайте директорию для вашего приложения
WORKDIR /app

# Скопируйте файл ChatServer.java в контейнер
COPY ChatServer.java .

# Скомпилируйте Java файл
RUN javac ChatServer.java

# Запустите сервер
CMD ["java", "ChatServer"]
