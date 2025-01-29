# Transaction Service
##  Запуск приложения

### 1. Запуск вручную

1. Убедитесь, что у вас установлены:
   - Java 21
   - PostgreSQL
   - Maven

2. Создайте базу данных в PostgreSQL:
   ```sql
   CREATE DATABASE transaction_db;
(Миграции находятся в src/main/resources/db/migration)

3. Настройте application.properties:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/transaction_db
spring.datasource.username=postgres
spring.datasource.password=12345
```
4. Запуск приложения 
```
mvn clean package
mvn spring-boot:run
```

### 2. Запуск через Docker
```
docker-compose up --build
```
Приложение будет доступно по адресу: http://localhost:8080

### 3. Документация API
Swagger-документация доступна по адресу: http://localhost:8080/swagger-ui.html

### 4.Тестирование
```
mvn test
```

