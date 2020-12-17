# Gateway

* This is a API Gateway which is implemented using Spring Cloud Gateway
* In this Layer traffic is router to downsteam microservices
* Circuit Breaker using Resilience4j is being used
* Request Rate Limiting is used with Redis
* Jaeger is configured for distributed tracing
* Prometheus endpoint is also enabled

## Request Rate Limiting with Redis

```
- name: RequestRateLimiter
  args: 
   redis-rate-limiter.replenishRate: 2000
   redis-rate-limiter.burstCapacity: 4000
   key-resolver: "#{@userRemoteAddressResolver}"
```
The **redis-rate-limiter.replenishRate** decide how many requests per second a user is allowed to send without any dropped requests.
The second property **redis-rate-limiter.burstCapacity** is the maximum number of requests a user is allowed to do in a single second. This is the number of tokens the token bucket can hold. Setting this value to zero will block all requests.


```
  redis:
    host: redis-master
    port: 6379
```
Redis should be running

```
@Bean(name = "userRemoteAddressResolver")
public KeyResolver userKeyResolver() {
  return exchange -> {
    return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
  };
}
```
Setting these 3 things will enable rate limiter on configured path

## Circuit Breaker with Resilience4j

Configure this on Path 
```
- name: CircuitBreaker
  args:
    name: backendA
    fallbackUri: forward:/fallback/ms2
    statusCodes:
      - 500

- name: CircuitBreaker
  args:
    name: backendA
    fallbackUri: forward:/fallback/ms1
    statusCodes:
      - 403
```

Configure the Circuit Breaker Properties
```
resilience4j.circuitbreaker:
  configs:
    default:
      register-health-indicator: true
      sliding-window-size: 10
      sliding-window-type: COUNT_BASED
      minimum-number-of-calls: 5
      writable-stack-trace-enabled: true     
      permitted-number-of-calls-in-half-open-state: 2
      automatic-transition-from-open-to-half-open-enabled: true
      wait-duration-in-open-state: 10s
      failure-rate-threshold: 50
  instances:
    backendA:
      baseConfig: default

    backendB:
      register-health-indicator: true
      sliding-window-size: 10
      sliding-window-type: COUNT_BASED
      minimum-number-of-calls: 5
      writable-stack-trace-enabled: true     
      permitted-number-of-calls-in-half-open-state: 2
      automatic-transition-from-open-to-half-open-enabled: true
      wait-duration-in-open-state: 10s
      failure-rate-threshold: 50
```

We have to configure this as well because if we configure Circuit Breaker, then by default request time out is configured as 1 Sec. Here we have configured the timeout as 5 Sec 
```
@Bean
public ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory(
    CircuitBreakerRegistry circuitBreakerRegistry) {
  ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory = new ReactiveResilience4JCircuitBreakerFactory();
  reactiveResilience4JCircuitBreakerFactory.configureCircuitBreakerRegistry(circuitBreakerRegistry);

  TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(5)).cancelRunningFuture(true)
            .build();
  reactiveResilience4JCircuitBreakerFactory.configure(builder -> builder.timeLimiterConfig(timeLimiterConfig).build(),"backendA","backendB");
  return reactiveResilience4JCircuitBreakerFactory;
}
```

## Distributed Tracing with Jaager

Add these to enable distributed tracing
```
opentracing:
  jaeger:
    enabled: true
    udp-sender:
      host: simplest-agent
      port: 6831 
    enable-b3-propagation: true
    log-spans: true
    const-sampler:
      decision: true
```

## keycloak integration

Add these to pom.xml
```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-oauth2-client</artifactId>
  </dependency>
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-oauth2-client</artifactId>
  </dependency>
<dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-oauth2-resource-server</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

Add these to application.yml
```
security:
    oauth2:
      client:
        provider:
          keycloak:
            token-uri: http://keycloak:8080/auth/realms/cloud/protocol/openid-connect/token
            authorization-uri: http://keycloak:8080/auth/realms/cloud/protocol/openid-connect/auth
            user-name-attribute: preferred_username
            user-info-uri: http://keycloak:8080/auth/realms/cloud/protocol/openid-connect/userinfo
            
            
        registration:
          spring-cloud-test:
            provider: keycloak
            client-id: spring-cloud-test
            client-secret: 5d301d6d-99f2-4995-bb12-2405dd98d669
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/keycloak"
```
Note :- Keycload should be publically available on http://keycloak:8080


We also need to add TokenRelay filter
```
spring:
  cloud:
    gateway:
      default-filters:
        - TokenRelay
```
