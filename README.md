# Spring Cloud Gateway with Resilience4j and Keycloak Integration

![Architecture](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/main/img/arch.PNG)

## This project contains 4 Spring boot Projects and Keycloak for access and identity management

* Gateway
* Micro Service 1
* Micro Service 2
* Micro Service 3

### Libraries and Tech Stack

* [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
* [Resilience4j](https://resilience4j.readme.io/docs)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Keycloak](https://www.keycloak.org/)

## Gateway

* This is a API Gateway which is implemented using Spring Cloud Gateway
* In this Layer traffic is router to downsteam microservices
* Circuit Breaker using Resilience4j is being used
* Request Rate Limiting is used with Redis
* Jaeger is configured for distributed tracing
* Prometheus endpoint is also enabled

### Request Rate Limiting with Redis

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

### Circuit Breaker with Resilience4j

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

### Distributed Tracing with Jaager

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


## Micro Service 1 (ms1)

* Webflux is used
* Resilience4j library is used
* Circuit Breaker, Bulkahead, Ratelimiter, Timelimiter, Retry and aggregate Pattern are implemented
* Jaeger is configured for distributed tracing
* Prometheus endpoint is also enabled
* Keycload is also enabled for token validation

### Circuit Breaker

Configure the Cricuit breaker configuration
```
resilience4j:
  retry:
    instances:
      retryService:
        max-retry-attempts: 3
        wait-duration: 5s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
        
  circuitbreaker:
    instances:
      mainService:
        minimum-number-of-calls: 5
        permitted-number-of-calls-in-half-open-state: 2
        wait-duration-in-open-state: 10s
        failure-rate-threshold: 50
        event-consumer-buffer-size: 10
        automatic-transition-from-open-to-half-open-enabled: true
        register-health-indicator: true
        sliding-window-size: 10
        sliding-window-type: COUNT_BASED
    circuit-breaker-aspect-order: 2
```


Enable the Circuit Breaker on any method. If any exception occurs in the method then circuit breaker will open
```
@GetMapping(value = "/checkrequest")
@CircuitBreaker(name = "mainService", fallbackMethod = "fallback")
@RateLimiter(name = "ratelimiterservice", fallbackMethod = "fallback")
@TimeLimiter(name = "timelimiterservice", fallbackMethod = "fallback")
@Bulkhead(name = "bulkaheadservice",fallbackMethod = "fallback")
@Retry(name="retryService")
@Timed(value = "TestController.checkrequest",description = "checkrequest" ,histogram = true)
public Mono<String> checkrequest() {

  System.out.println("inside checkrequest");

  Mono<String> mono2 = ms2WebClient.get().uri("/checkrequestcall").retrieve().bodyToMono(String.class);

  Mono<String> mono3 = ms3WebClient.get().uri("/checkrequestcall").retrieve().bodyToMono(String.class);

  return Mono.zip(mono2, mono3).map(tuple -> {
    return tuple.getT2();
  });

}
```
