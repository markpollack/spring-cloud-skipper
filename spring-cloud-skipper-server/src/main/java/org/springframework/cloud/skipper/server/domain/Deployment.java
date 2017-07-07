package org.springframework.cloud.skipper.server.domain;

import java.util.Map;

/**
 * This class hold the data used to call the spring-cloud-deployer APIs
 */
public class Deployment {

	private String name;

	private int count;

	private Map<String, String> labels;

	private Map<String, String> applicationProperties;

	private String resource;

	private String resourceMetadata;

	private Map<String, String> deploymentProperties;

	public Deployment() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Map<String, String> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}

	public Map<String, String> getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Map<String, String> applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getResourceMetadata() {
		return resourceMetadata;
	}

	public void setResourceMetadata(String resourceMetadata) {
		this.resourceMetadata = resourceMetadata;
	}

	public Map<String, String> getDeploymentProperties() {
		return deploymentProperties;
	}

	public void setDeploymentProperties(Map<String, String> deploymentProperties) {
		this.deploymentProperties = deploymentProperties;
	}
}
