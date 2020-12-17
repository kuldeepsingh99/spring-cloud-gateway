# Spring Cloud Gateway with Resilience4j and Keycloak Integration

![Architecture](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/img/arch.PNG)

## This project contains 4 Spring boot Projects and Keycloak for access and identity management

* Gateway
* Micro Service 1
* Micro Service 2
* Micro Service 3

## Libraries and Tech Stack

* [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway) - as an API Gateaway
* [Resilience4j](https://resilience4j.readme.io/docs) - for resilience patterns
* [Spring Boot](https://spring.io/projects/spring-boot) - to build microservices
* [Keycloak](https://www.keycloak.org/) - for identity and access managment
* [Jaeger](https://www.jaegertracing.io/) - for distributed tracing
* [Prometheus](https://prometheus.io/) - for monitoring
* [Grafana](https://grafana.com/) - for monitoring
* [Loki](https://grafana.com/docs/loki/latest/getting-started/get-logs-into-loki/) - for log monitoring

## GateWay and Microservice Configuration

Please refer [Gateway](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/gateway/README.md) readme for Spring Cloud Gateway, Resilience4j and Keycloak configuration

Please refer [Micro service 1](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/ms1/README.md) readme for Resilience4j and Keycloak configuration

## Deployment 

We have deployed everything on [kubernetes](https://kubernetes.io/)

## Steps for Deployment

### Creating NameSpace

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/namespace.yml) to create namespace

kubectl apply -f namespace.yml

### Create redis Instance

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/redis.yml) to create redis instance

kubectl apply -f redis.yml


### Create NGINX Ingress Controller

//TODO

### Create Keycloak Instance

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/keycloak.yml) to setup keycloak

kubectl apply -f keycloak.yml

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/keycloakingress.yml) to setup keycloak Ingress controller

kubectl apply -f keycloakingress.yml

Note :- After Keycload is up and Running create realm and configure client


### Create Gateway Instance
//TODO

### Create Microservices instance

//TODO
### Install Prometheus

//TODO
### Install Grafana
//TODO

### Install Jaeger
