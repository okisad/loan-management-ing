FROM maven:3.9.5-amazoncorretto-21
WORKDIR /app
COPY target/*.jar credit-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "credit-app.jar"]