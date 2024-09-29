package com.khantech.gaming.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TransactionManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionManagementSystemApplication.class, args);
	}

}
