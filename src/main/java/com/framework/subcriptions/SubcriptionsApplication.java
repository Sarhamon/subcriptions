package com.framework.subcriptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SubcriptionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubcriptionsApplication.class, args);
	}

}
