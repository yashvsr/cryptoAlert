package com.greex.price_alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "com.greex.price_alert.repository")
@EntityScan(basePackages = "com.greex.price_alert.entity")
@EnableScheduling
public class PriceAlertApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceAlertApplication.class, args);
	}

}
