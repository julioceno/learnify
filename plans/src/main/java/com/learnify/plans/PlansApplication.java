package com.learnify.plans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PlansApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlansApplication.class, args);
	}

}
