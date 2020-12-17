FROM java:8
COPY /target/ms3-0.0.1-SNAPSHOT.jar ms3.jar
EXPOSE 8085
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ms3.jar"]