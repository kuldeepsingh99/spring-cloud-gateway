package com.portal.ms1.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.portal.ms1.TestService;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.core.annotation.Timed;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ms1")
public class TestController {

	@Autowired
	WebClient ms2WebClient;

	@Autowired
	WebClient ms3WebClient;

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	TestService testService;

	@GetMapping(value = "/getdata")
	public Mono<String> getData() {
		System.out.println("Inside SC-MS1 getData method");
		
		Mono<String> data = Mono.just("Hello from Reactive SC-MS1 getData method!!");
		return data;
	}

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

	@GetMapping("/ping")
	@Timed(value = "TestController.ping",description = "ping" ,histogram = true)
	public String ping() {
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return testService.ping();
	}
	
	@GetMapping("/admin")
	@Timed(value = "TestController.admin",description = "admin" ,histogram = true)
	public String pingadmin() {
		return "admin";
	}

	public Mono<String> fallback(BulkheadFullException ex) {
		System.out.println("---------------------------------------------BulkheadFullException-----------------------------------");
		Mono<String> data = Mono.just("BulkheadFullException");
		return data;
	}

	public Mono<String> fallback(CallNotPermittedException ex) {
		System.out.println("---------------------------------------------CB OPEN-----------------------------------");
		Mono<String> data = Mono.just("CB OPEN");
		return data;
	}

	public Mono<String> fallback(HttpServerErrorException ex) {
		System.out
				.println("-------------------------------------------- Exception -----------------------------------");
		Mono<String> data = Mono.just("Exception");
		return data;
	}

	public Mono<String> fallback(RequestNotPermitted ex) {
		System.out.println(
				"-------------------------------------------- RAte Limited -----------------------------------");
		Mono<String> data = Mono.just("RAte");
		return data;
	}

	public Mono<String> fallbackcb(RuntimeException ex) {
		System.out.println(
				"---------------------------------------------FALLBACK1 cb-----------------------------------");
		Mono<String> data = Mono.just("hello ms1 CB fallback");
		return data;
	}

	public Mono<String> fallbackcb(Throwable ex) {
		System.out.println(
				"---------------------------------------------FALLBACK 2 cb-----------------------------------");
		Mono<String> data = Mono.just("hello ms1 CB fallback");
		return data;
	}

	public Mono<String> fallbackretry(Throwable ex) {
		System.out.println(
				"---------------------------------------------FALLBACK RETRY-----------------------------------");
		Mono<String> data = Mono.just("RETRY");
		return data;
	}
}
