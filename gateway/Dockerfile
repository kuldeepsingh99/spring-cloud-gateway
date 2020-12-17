FROM java:8
COPY /target/gateway-0.0.1-SNAPSHOT.jar gateway.jar
EXPOSE 9090
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/gateway.jar"]