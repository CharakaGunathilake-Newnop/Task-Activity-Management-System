# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# 1. Copy the Maven wrapper and pom.xml files first to cache dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# For multi-module projects, copy all module poms
COPY common/pom.xml common/
COPY identity-module/pom.xml identity-module/
COPY task-module/pom.xml task-module/
COPY activity-module/pom.xml activity-module/
COPY admin-module/pom.xml admin-module/
COPY infrastructure/pom.xml infrastructure/

# 2. Download dependencies (this layer is cached unless poms change)
RUN ./mvnw dependency:go-offline

# 3. Copy the source code and build the application
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create a non-root user for better security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Copy the "fat JAR" from the infrastructure module's target folder
# Note: Adjust the path/name if your artifact name differs
COPY --from=build /app/infrastructure/target/*.jar app.jar

# Expose the standard Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]