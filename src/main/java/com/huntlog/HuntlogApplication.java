package com.huntlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HuntlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(HuntlogApplication.class, args);
	}

}
