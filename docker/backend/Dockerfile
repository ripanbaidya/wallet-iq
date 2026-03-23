# ============================================================
# Dockerfile
# ============================================================

# ---- Stage 1: Build ----
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
# Set /app as current directory inside container
WORKDIR /app

# Copy pom.xml first — Docker caches this layer if pom.xml doesn't change.
# This means dependencies are only re-downloaded when pom.xml changes.
# flag -B means, no interactive logs(clean CI logs)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for security
# Create group walletiq and user walletiq
RUN addgroup -S walletiq && adduser -S walletiq -G walletiq

# Copy the fat jar from build stage
COPY --from=builder /app/target/*.jar app.jar

# Render injects PORT env var; Spring uses SERVER_PORT
ENV SERVER_PORT=8080

EXPOSE 8080

# Switch to non-root, now run app as this user
USER walletiq

# Start the app
# -XX:+UseContainerSupport — JVM respects container memory limits
# -XX:MaxRAMPercentage=75  — use 75% of container RAM for heap
# -Djava.security.egd=file:/dev/./urandom - Faster random number generation (important for startup)
# -jar", "app.jar - Runs Spring Boot fat jar
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]