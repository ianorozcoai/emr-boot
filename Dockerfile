FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# Use your actual build command
RUN mvn -Pprod -DskipTests clean install

FROM eclipse-temurin:21-jdk-jammy
RUN apt-get update && apt-get install -y \
    libfreetype6 \
    fontconfig \
    fonts-dejavu \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app/target/emr.jar app.jar

CMD ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]