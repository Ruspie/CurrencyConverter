# ===== Stage 1: сборка =====
# Берём готовый образ с Maven + JDK 17 и собираем jar внутри контейнера.
# Так на машине не обязательно иметь установленный Maven.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Сначала только pom — Docker закэширует слой с зависимостями,
# и при правках кода не будет качать всё заново.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package \
    && cp /app/target/CurrencyConverter-*.jar /app/app.jar \
    && rm -f /app/app.jar.original

# ===== Stage 2: запуск =====
# Лёгкий образ только с JRE (без Maven и исходников) — меньше размер и безопаснее.
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Имя jar зависит от <version> в pom.xml — копируем уже нормализованный app.jar
COPY --from=build /app/app.jar app.jar

# Spring Boot по умолчанию слушает 8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
