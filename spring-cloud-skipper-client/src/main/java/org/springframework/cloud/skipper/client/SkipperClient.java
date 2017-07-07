package org.springframework.cloud.skipper.client;

import org.springframework.cloud.skipper.api.*;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.domain.SkipperPackage;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

public class SkipperClient {

	private RestTemplate restTemplate;

	private String baseUri;

	private PackageLoader packageLoader;

	public SkipperClient(RestTemplate restTemplate, String baseUri, PackageLoader packageLoader) {
		Assert.notNull(restTemplate, "SkipperClient: RestTemplate required.");
		Assert.hasText(baseUri, "SkipperClient: baseUri required");
		Assert.notNull(packageLoader, "SkipperClient: PackageLoader required.");
		this.restTemplate = restTemplate;
		this.baseUri = baseUri;
		this.packageLoader = packageLoader;
	}

	public String version() {
		return restTemplate.getForObject(baseUri + "/version", String.class);
	}

	public Release install(String packagePath, String releaseName) {
		SkipperPackage skipperPackage = packageLoader.load(packagePath);
		InstallReleaseRequest request = new InstallReleaseRequest(releaseName, skipperPackage);
		return restTemplate.postForObject(baseUri + "/install", request, Release.class);
	}

	public Release update(String packagePath, String releaseName) {
		SkipperPackage skipperPackage = packageLoader.load(packagePath);
		UpdateReleaseRequest request = new UpdateReleaseRequest(releaseName, skipperPackage);
		return restTemplate.postForObject(baseUri + "/update", request, Release.class);
	}

	public Release rollback(String releaseName, Integer releaseVersion) {
		RollbackReleaseRequest request = new RollbackReleaseRequest(releaseName, releaseVersion);
		return restTemplate.postForObject(baseUri + "/rollback", request, Release.class);
	}

	public Release[] history(String releaseName) {
		HistoryRequest request = new HistoryRequest(releaseName);
		HistoryResponse response = restTemplate.postForObject(baseUri + "/history", request,
				HistoryResponse.class);
		return response.getReleases();
	}
}
