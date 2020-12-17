FROM java:8
COPY /target/ms2-0.0.1-SNAPSHOT.jar ms2.jar
EXPOSE 9092
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ms2.jar"]