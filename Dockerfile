FROM maven:3.9-eclipse-temurin-17 as build
WORKDIR /app

# ----------------------------------------------------------------

COPY pom.xml .

RUN mvn -B -q dependency:go-offline

COPY src ./src

RUN mvn -B -DskipTests package  \
    && cp /app/target/CurrencyConverter-*.jar /app/app.jar \
    && rm -f /app/app.jar.original

#-------------------------------------------------------------

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]




