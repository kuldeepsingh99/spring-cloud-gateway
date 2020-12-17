package com.portal.gateway.controller;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class TestController {

	@GetMapping(value = "/ms2")
	public Mono<String> getData(ServerHttpRequest request, ServerHttpResponse response) {

		Mono<String> data = Mono.just("fallback message ms2");
		return data;
	}

	
	@GetMapping(value = "/ms3")
	public Mono<String> get3Data(ServerHttpRequest request, ServerHttpResponse response) {

		Mono<String> data = Mono.just("fallback message ms 3");
		
		return data;
	}
	
	@GetMapping(value = "/token")
    public Mono<String> getHome(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
      return Mono.just(authorizedClient.getAccessToken().getTokenValue()+"--->"+authorizedClient.getRefreshToken().getTokenValue());
    }
	
	@GetMapping(value = "/ms1")
	public Mono<String> getDatams1(ServerHttpRequest request, ServerHttpResponse response) {

		Mono<String> data = Mono.just("Access Denied");
		return data;
	}
	
	@GetMapping(value = "/logout")
	public Mono<String> logout(ServerHttpRequest request) {
		
		Mono<String> data = Mono.just("Logout");
		return data;
	}
}
