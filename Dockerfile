FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S springboot && adduser -S springboot -G springboot
WORKDIR /app

COPY target/*.jar app.jar
RUN chown -R springboot:springboot /app

USER springboot:springboot
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
