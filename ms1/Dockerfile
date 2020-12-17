FROM java:8
COPY /target/ms1-0.0.1-SNAPSHOT.jar ms1.jar
EXPOSE 9091
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ms1.jar"]