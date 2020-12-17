package com.portal.ms2.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import reactor.core.publisher.Mono;

@RestController
public class TestConroller {

	@GetMapping(value = "/slow-call")
	@Timed(value = "TestController.slow-call",description = "slowcall" ,histogram = true)
	public Mono<ResponseEntity<String>> getData(ServerHttpRequest request, ServerHttpResponse response)
			throws Exception {

		System.out.println("-----inside slow-call method ------------");
		Thread.sleep(2000);

		Mono<ResponseEntity<String>> data = Mono
				.just(new ResponseEntity<String>("Hello from Reactive SC-MS2 slow call method!!", HttpStatus.OK));
		return data;
	}
	
	@GetMapping(value = "/exception-call")
	@Timed(value = "TestController.exception-call",description = "exceptioncall" ,histogram = true)
	public Mono<ResponseEntity<String>> exceptionCall(ServerHttpRequest request, ServerHttpResponse response)
			throws Exception {
		System.out.println("-----inside exception-call method ------------");
		Mono<ResponseEntity<String>> data = Mono
				.just(new ResponseEntity<String>("Hello from Reactive SC-MS2 exception call method!!", HttpStatus.INTERNAL_SERVER_ERROR));
		
		return data;
	}
	


	@GetMapping(value = "/checkrequestcall")
	@Timed(value = "TestController.checkrequestcall",description = "checkrequestcall" ,histogram = true)
	public Mono<ResponseEntity<String>> checkrequestcall(ServerHttpRequest request, ServerHttpResponse response) {
		Mono<ResponseEntity<String>> data = Mono
				.just(new ResponseEntity<String>("MS2", HttpStatus.OK));
		return data;
	}
}
