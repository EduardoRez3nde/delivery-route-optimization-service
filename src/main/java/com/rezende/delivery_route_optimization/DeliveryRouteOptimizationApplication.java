package com.rezende.delivery_route_optimization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DeliveryRouteOptimizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryRouteOptimizationApplication.class, args);
	}

}
