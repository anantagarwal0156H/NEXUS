FROM node:22-alpine AS web
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install --no-audit --no-fund
COPY frontend/ ./
RUN npm run build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/pom.xml ./
COPY backend/src ./src
COPY --from=web /app/dist ./src/main/resources/static
RUN mvn -B clean verify
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/nexus-0.1.0.jar ./nexus.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=65.0","-jar","/app/nexus.jar"]
