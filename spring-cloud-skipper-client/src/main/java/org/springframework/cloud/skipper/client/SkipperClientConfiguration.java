package org.springframework.cloud.skipper.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(SkipperClientConfigurationProperties.class)
public class SkipperClientConfiguration {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		// Do any additional configuration here
		return builder.build();
	}

	@Bean
	public PackageLoader packageLoader() {
		return new PackageLoader();
	}

	@Bean
	public SkipperClient skipperClient(RestTemplate restTemplate,
			SkipperClientConfigurationProperties skipperClientConfigurationProperties,
			PackageLoader packageLoader) {
		return new SkipperClient(restTemplate, skipperClientConfigurationProperties.getHost(),
				packageLoader);

	}
}
