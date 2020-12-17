package com.portal.ms3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;

@SpringBootApplication
public class Ms3Application {

	public static void main(String[] args) {
		SpringApplication.run(Ms3Application.class, args);
	}
	
	@Bean
    MeterRegistryCustomizer<PrometheusMeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("ms3", "com.portal.ms2");
    }
    @Bean
    TimedAspect timedAspect(MeterRegistry reg) {
        return new TimedAspect(reg);
    }

}
