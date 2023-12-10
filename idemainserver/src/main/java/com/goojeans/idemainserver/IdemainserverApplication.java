package com.goojeans.idemainserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class IdemainserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdemainserverApplication.class, args);
	}

}
