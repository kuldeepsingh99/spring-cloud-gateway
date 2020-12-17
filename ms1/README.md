# Micro Service 1 (ms1)

* Webflux is used
* Resilience4j library is used
* Circuit Breaker, Bulkahead, Ratelimiter, Timelimiter, Retry and aggregate Pattern are implemented
* Jaeger is configured for distributed tracing
* Prometheus endpoint is also enabled
* Keycload is also enabled for token validation

## [Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker)

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

## [Rate Limiter](https://resilience4j.readme.io/docs/ratelimiter)

Rate limiting is an imperative technique to prepare your API for scale and establish high availability and reliability of your service. In this example 100 calls are allowed in 5 sec

Configure rate limiter in application.yml

```
ratelimiter:
  instances:
    ratelimiterservice:
      limit-for-period: 100
      limit-refresh-period: 5s
      timeout-duration: 0
      allow-health-indicator-to-fail: true
      register-health-indicator: true        
  rate-limiter-aspect-order: 1
```

Apply Rate Limiter on any method with annotatations
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

## [Timelimiter](https://resilience4j.readme.io/docs/timeout)

This work like a timeout, as per this example 5sec is configured

```
timelimiter:
  instances:
    timelimiterservice:
      timeout-duration: 5s
      cancel-running-future: true 
```
Apply Time Limiter on any method with annotatations
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

## [Bulkahead](https://resilience4j.readme.io/docs/bulkhead)

In a particular time how many request can the API Process, as per this example 30 calls
```
bulkhead:
  instances:
    bulkaheadservice:
      max-concurrent-calls: 30
      max-wait-duration: 0    
  metrics:
    enabled: true
```
Apply Bulkahead on any method with annotatations
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

## [Retry](https://resilience4j.readme.io/docs/retry)

This configuration will retry the call, as per this example method will retry 3 times in a Second
```
resilience4j:
  retry:
    instances:
      retryService:
        max-retry-attempts: 3
        wait-duration: 5s
        enable-exponential-backoff: true
        exponential-backoff-multiplier: 2
```

Apply Retry on any method with annotatations
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

## Aggregate Pattern

As we have used web reactive Library (WebFlux) on all the microservice, so the below mentioned tto calls to MS2 Service and M23 Service wil go in parallel

```
public Mono<String> checkrequest() {

  System.out.println("inside checkrequest");

  Mono<String> mono2 = ms2WebClient.get().uri("/checkrequestcall").retrieve().bodyToMono(String.class);

  Mono<String> mono3 = ms3WebClient.get().uri("/checkrequestcall").retrieve().bodyToMono(String.class);

  return Mono.zip(mono2, mono3).map(tuple -> {
    return tuple.getT2();
  });
}
```

## Spring Micrometer with prometheus

@Timed annotation is used to track the method calls in prometheus

```
@Timed(value = "TestController.checkrequest",description = "checkrequest" ,histogram = true)
public Mono<String> checkrequest() {
```

```
@Bean
MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry) {
  return meterRegistry1 -> {
    meterRegistry.config()
        .commonTags("application", "micrometer-monitoring-example");
  };
}

@Bean
  TimedAspect timedAspect(MeterRegistry reg) {
      return new TimedAspect(reg);
  }
```

These two things are required to configure Spring micrometer


## Distributed Tracking with Jaeger

Add these in application.yml to enable distributed tracing

```
opentracing:
  jaeger:
    enabled: true
    service-name: ms1-app-server
    udp-sender:
      host: simplest-agent
      port: 6831 
    enable-b3-propagation: true
    log-spans: true
    const-sampler:
      decision: true
```

## How to enable keycloak in Spring boot

Add these dependencies to pom.xml, this may be different if we don't have Spring Cloud Gateway
```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
<dependency>
  <groupId>org.keycloak</groupId>
  <artifactId>keycloak-spring-security-adapter</artifactId>
  <version>6.0.1</version>
</dependency>
```

Add these in application.yml

```
keycloak:
  auth-server-url: http://keycloak:8080/auth/
  resource: spring-cloud-test
  credentials:
    secret : 5d301d6d-99f2-4995-bb12-2405dd98d669
  use-resource-role-mappings : true
  principal-attribute: preferred_username
  bearer-only: true
  realm: cloud
```


```
@Bean
public KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver()
{
    return new KeycloakSpringBootConfigResolver();
}
```

Configure this class
[Security Config](https://github.com/kuldeepsingh99/spring-cloud-gateway/blob/dc7a833c56e185d06de4b107b3917c97d71a726a/ms1/src/main/java/com/portal/ms1/SecurityConfig.java#L20) for keycload and role mapping
