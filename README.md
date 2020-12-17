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

Execute this [micro service 1 Config Map](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms1configmap.yml) file to create config Map for micro service 1, this is used to inject other service URL and Port

We can create config map based on environment and we don't have to change code on different envoronments 

```kubectl apply -f ms1configmap.yml```

Execute this [micro service 1](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms1.yml) file to create micro service 1 instance

```kubectl apply -f ms1.yml```

Execute this [micro service 2](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms2.yml) file to create micro service 2 instance

```kubectl apply -f ms2.yml```

Execute this [micro service 3](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms3.yml) file to create micro service 3 instance

```kubectl apply -f ms3.yml```

By Default Cluster IP is assigned to Services, and its accessible inside the CLuster only. We need to create ingress controller so that its accessible from outside





### Install Prometheus

//TODO
### Install Grafana
//TODO

### Install Jaeger
