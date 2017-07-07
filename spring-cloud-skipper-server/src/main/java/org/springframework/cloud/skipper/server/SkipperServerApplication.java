package org.springframework.cloud.skipper.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @ComponentScan("org.springframework.cloud.skipper")
public class SkipperServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkipperServerApplication.class, args);
	}

}
