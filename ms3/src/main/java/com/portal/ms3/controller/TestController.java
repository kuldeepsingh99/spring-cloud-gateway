package com.portal.ms3.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

	
	@GetMapping(value = "/checkrequestcall")
	@Timed(value = "TestController.checkrequestcall",description = "checkrequestcallms3" ,histogram = true)
	public Mono<ResponseEntity<String>> checkrequestcall(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
		Mono<ResponseEntity<String>> data = Mono
				.just(new ResponseEntity<String>("MS3", HttpStatus.OK));
		
		return data;
	}
}
