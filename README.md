# Spring Cloud Gateway with Resilience4j and Keycloak Integration

![Architecture](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/img/arch.PNG)

## This project contains 4 Spring boot Projects and Keycloak for access and identity management

* [Gateway](https://github.com/kuldeepsingh99/spring-cloud-gateway/tree/main/gateway)  - (This is an API Gateway, where spring cloud gateway is used with Resilience4j and keycloak integration)
* [Micro Service 1](https://github.com/kuldeepsingh99/spring-cloud-gateway/tree/main/ms1)  - (This is a reative microservice where Resilience4j patterns are implemented with keycloak integration)
* [Micro Service 2](https://github.com/kuldeepsingh99/spring-cloud-gateway/tree/main/ms2)  - (This is a reative microservice)
* [Micro Service 3](https://github.com/kuldeepsingh99/spring-cloud-gateway/tree/main/ms3)  - (This is a reative microservice)

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

```kubectl apply -f namespace.yml```

### Create redis Instance

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/redis.yml) to create redis instance

```kubectl apply -f redis.yml```


### Create NGINX Ingress Controller

Execute this to deploy NGINX Ingress controller

```kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/static/provider/aws/deploy.yaml```

Verify pod progress

```kubectl get pods -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx --watch```

### Install Prometheus

Install helm chart

Execute this command to deploy prometheus

```
helm repo add stable https://kubernetes-charts.storage.googleapis.com/
helm repo update
```

```helm install -name prom -n monitor stable/prometheus-operator```

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/promingress.yml) to setup ingress for Prometheus

```kubectl apply -f promingress.yml```

Verify pod progress

```kubectl get pods -n monitor```

Try accessing jaeger with http://prometheus.practice.com

### Install Grafana

Execute this command to deploy Grafana

```helm install -name graph -n monitor stable/grafana```

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/grafanaingress.yml) to setup ingress for Grafana

```kubectl apply -f grafanaingress.yml```

Try accessing jaeger with http://grafana.practice.com

### Install Jaeger

Execute the following command

```
kubectl create -n monitor -f https://raw.githubusercontent.com/jaegertracing/jaeger-operator/master/deploy/crds/jaegertracing.io_jaegers_crd.yaml
kubectl create -n monitor -f https://raw.githubusercontent.com/jaegertracing/jaeger-operator/master/deploy/service_account.yaml
kubectl create -n monitor -f https://raw.githubusercontent.com/jaegertracing/jaeger-operator/master/deploy/role.yaml
kubectl create -n monitor -f https://raw.githubusercontent.com/jaegertracing/jaeger-operator/master/deploy/role_binding.yaml
kubectl create -n monitor -f https://raw.githubusercontent.com/jaegertracing/jaeger-operator/master/deploy/operator.yaml
```

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/jaeger_new.yml) to deploy jaeger

```kubectl apply -f jaeger_new.yml```

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/jaegeringress.yml) to setup ingress for jaeger

```kubectl apply -f jaegeringress.yml```

Verify pod progress

```kubectl get pods -n monitor```

Try accessing jaeger with http://jaeger.practice.com

### Create Keycloak Instance

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/keycloak.yml) to setup keycloak

```kubectl apply -f keycloak.yml```

Execute this [file](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/keycloakingress.yml) to setup keycloak Ingress controller

```kubectl apply -f keycloakingress.yml```

Note :- After Keycload is up and Running create realm and configure client


### Create Gateway Instance

Execute this [gateway](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/gateway.yml) file to create gateway instance

```kubectl apply -f gateway.yml```

Execute this [gateway ingress](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/gatewayingress.yml) file to create gateway ingress

```kubectl apply -f gatewayingress.yml```

Try accessing http://test.practice.com

How Prometheus know where to scrap prometheus end, we need to create service monitor

Execute this [gateway service monitor](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/gatewayservicemonitor.yml) file to create gateway service monitor

```kubectl apply -f gatewayservicemonitor.yml```

Check the target in Prometheus

### Create Microservices instance

Execute this [micro service 1 config map](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms1configmap.yml) file to create config Map for micro service 1, this is used to inject other service URL and Port

We can create config map based on environment and we don't have to change code on different envoronments 

```kubectl apply -f ms1configmap.yml```

Execute this [micro service 1](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms1.yml) file to create micro service 1 instance

```kubectl apply -f ms1.yml```

Execute this [micro service 2](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms2.yml) file to create micro service 2 instance

```kubectl apply -f ms2.yml```

Execute this [micro service 3](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms3.yml) file to create micro service 3 instance

```kubectl apply -f ms3.yml```

Verify pod progress

```kubectl get pods -n monitor```

Create service Monitor for these services [micro service 1](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms1servicemonitor.yml), [micro service 2](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms2servicemonitor.yml) and [micro service 3](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/deployment/ms3servicemonitor.yml)


```
kubectl apply -f ms1servicemonitor.yml
kubectl apply -f ms2servicemonitor.yml
kubectl apply -f ms3servicemonitor.yml
```

Verify the Target in Prometheus

