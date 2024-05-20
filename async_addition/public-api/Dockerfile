FROM eclipse-temurin:21-alpine
VOLUME /tmp
COPY target/public-addition-0.0.1-SNAPSHOT.jar /home/node/app/public-addition.jar
ENTRYPOINT ["java","-jar","/home/node/app/public-addition.jar"]