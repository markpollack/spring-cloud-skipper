package org.springframework.cloud.skipper.server;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.deployer.resource.docker.DockerResourceLoader;
import org.springframework.cloud.deployer.resource.maven.MavenProperties;
import org.springframework.cloud.deployer.resource.maven.MavenResourceLoader;
import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.map.repository.config.EnableMapRepositories;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
//@EnableRedisRepositories
@EnableMapRepositories
public class SkipperServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkipperServerApplication.class, args);
	}

	@Bean
	public MavenProperties mavenProperties() {
		return new MavenConfigurationProperties();
	}

	@Bean
	public DelegatingResourceLoader delegatingResourceLoader(MavenProperties mavenProperties) {
		DockerResourceLoader dockerLoader = new DockerResourceLoader();
		MavenResourceLoader mavenResourceLoader = new MavenResourceLoader(mavenProperties);
		Map<String, ResourceLoader> loaders = new HashMap<>();
		loaders.put("docker", dockerLoader);
		loaders.put("maven", mavenResourceLoader);
		return new DelegatingResourceLoader(loaders);
	}

	@ConfigurationProperties(prefix = "maven")
	static class MavenConfigurationProperties extends MavenProperties {
	}
}
