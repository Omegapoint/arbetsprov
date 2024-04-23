FROM openjdk:17

WORKDIR /app
EXPOSE 8443
COPY build/libs/*.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]

