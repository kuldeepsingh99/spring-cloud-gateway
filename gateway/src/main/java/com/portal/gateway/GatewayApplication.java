package com.portal.gateway;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.gateway.filter.factory.FallbackHeadersGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@GetMapping(value = "/token")
	public Mono<String> getHome(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
		return Mono.just(authorizedClient.getAccessToken().getTokenValue());
	}

	@GetMapping("/")
	public Mono<String> index(WebSession session) {
		return Mono.just(session.getId());
	}
	
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
	
	@Bean
    public FallbackHeadersGatewayFilterFactory fallbackHeadersGatewayFilterFactory() {
        return new FallbackHeadersGatewayFilterFactory();
    }
    
	
	@Bean(name = "userRemoteAddressResolver")
	public KeyResolver userKeyResolver() {
		return exchange -> {
			return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
		};
	}
	
}
