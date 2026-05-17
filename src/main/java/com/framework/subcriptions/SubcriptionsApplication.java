package com.framework.subcriptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// Spring Boot 애플리케이션 진입점. @EnableScheduling으로 @Scheduled(환율 갱신) 활성화.
@SpringBootApplication
@EnableScheduling
public class SubcriptionsApplication {

	// JVM이 호출하는 main. 내장 Tomcat과 Spring 컨테이너를 부트스트랩한다.
	public static void main(String[] args) {
		SpringApplication.run(SubcriptionsApplication.class, args);
	}

}
