/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.skipper.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * This class hold the data used to call the spring-cloud-deployer APIs, deserialized from
 * a YML file.
 */
public class Deployment {

	private String name;

	private int count;

	private Map<String, String> labels = new HashMap<>();

	private Map<String, String> applicationProperties = new HashMap<>();

	private String resource;

	private String resourceMetadata;

	private Map<String, String> deploymentProperties = new HashMap<>();

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
		if (applicationProperties == null) {
			return new HashMap<>();
		}
		else {
			return applicationProperties;
		}
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
		if (deploymentProperties == null) {
			return new HashMap<>();
		}
		else {
			return deploymentProperties;
		}

	}

	public void setDeploymentProperties(Map<String, String> deploymentProperties) {
		this.deploymentProperties = deploymentProperties;
	}
}
