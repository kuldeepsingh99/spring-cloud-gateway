package com.portal.ms1;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
public class Ms1Application {

	@Value("${ms2.serviceurl}")
	private String ms2Url;
	
	@Value("${ms2.serviceurl.port}")
	private String ms2Port;
	
	@Value("${ms3.serviceurl}")
	private String ms3Url;
	
	@Value("${ms3.serviceurl.port}")
	private String ms3Port;
	
	public static void main(String[] args) {
		SpringApplication.run(Ms1Application.class, args);
	}
	
	@Bean
    public KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver()
    {
        return new KeycloakSpringBootConfigResolver();
    }

	@Bean
	public WebClient ms2WebClient() {
		System.out.println(ms2Url);
		return WebClient.create("http://"+ms2Url+":"+ms2Port);
	}
	
	@Bean
	public WebClient ms3WebClient() {
		return WebClient.create("http://"+ms3Url+":"+ms3Port);
	}
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
	
	
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

}
