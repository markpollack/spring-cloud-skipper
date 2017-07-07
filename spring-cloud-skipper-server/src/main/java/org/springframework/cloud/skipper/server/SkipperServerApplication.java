package org.springframework.cloud.skipper.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class SkipperServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkipperServerApplication.class, args);
	}

}
