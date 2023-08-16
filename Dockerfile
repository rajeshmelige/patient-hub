FROM eclipse-temurin:17-jdk-focal

WORKDIR /app
COPY ./target/patient.hub-0.0.1-SNAPSHOT.jar /app

EXPOSE 8083

CMD ["java", "-jar", "patient.hub-0.0.1-SNAPSHOT.jar"]