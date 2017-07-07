package org.springframework.cloud.skipper.client;

import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class SkipperClient {

	private RestTemplate restTemplate;

	private String baseUri;

	public SkipperClient(RestTemplate restTemplate, String baseUri) {
		Assert.notNull(restTemplate, "SkipperClient: RestTemplate required.");
		Assert.hasText(baseUri, "SkipperClient: baseUri required");
		this.restTemplate = restTemplate;
		this.baseUri = baseUri;
	}

	public String version() {
		return restTemplate.getForObject(baseUri + "/version", String.class);
	}

}
