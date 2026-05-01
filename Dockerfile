# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY todo-service/pom.xml .
COPY todo-service/.mvn .mvn
COPY todo-service/mvnw .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY todo-service/src src

RUN ./mvnw clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
