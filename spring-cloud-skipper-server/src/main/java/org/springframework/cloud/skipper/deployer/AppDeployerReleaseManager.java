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
package org.springframework.cloud.skipper.deployer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.app.AppInstanceStatus;
import org.springframework.cloud.deployer.spi.app.AppStatus;
import org.springframework.cloud.deployer.spi.app.DeploymentState;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.skipper.SkipperException;
import org.springframework.cloud.skipper.domain.AppDeployerData;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.domain.SpringBootAppKind;
import org.springframework.cloud.skipper.domain.SpringBootAppKindReader;
import org.springframework.cloud.skipper.domain.SpringBootAppSpec;
import org.springframework.cloud.skipper.domain.Status;
import org.springframework.cloud.skipper.domain.StatusCode;
import org.springframework.cloud.skipper.repository.DeployerRepository;
import org.springframework.cloud.skipper.repository.ReleaseRepository;
import org.springframework.cloud.skipper.service.ReleaseAnalysisReport;
import org.springframework.cloud.skipper.service.ReleaseAnalysisService;
import org.springframework.cloud.skipper.service.ReleaseManager;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * A ReleaseManager implementation that uses the AppDeployer.
 *
 * @author Mark Pollack
 * @author Ilayaperumal Gopinathan
 */
@Service
public class AppDeployerReleaseManager implements ReleaseManager {

	private final ReleaseRepository releaseRepository;

	private final DelegatingResourceLoader delegatingResourceLoader;

	private final AppDeployerDataRepository appDeployerDataRepository;

	private final DeployerRepository deployerRepository;

	private final ReleaseAnalysisService releaseAnalysisService;

	@Autowired
	public AppDeployerReleaseManager(ReleaseRepository releaseRepository,
			AppDeployerDataRepository appDeployerDataRepository,
			DelegatingResourceLoader delegatingResourceLoader,
			DeployerRepository deployerRepository,
			ReleaseAnalysisService releaseAnalysisService) {
		this.releaseRepository = releaseRepository;
		this.appDeployerDataRepository = appDeployerDataRepository;
		this.delegatingResourceLoader = delegatingResourceLoader;
		this.deployerRepository = deployerRepository;
		this.releaseAnalysisService = releaseAnalysisService;
	}

	public Release install(Release releaseInput) {

		Release release = this.releaseRepository.save(releaseInput);

		// Deploy the application
		SpringBootAppKindReader springBootAppKindReader = new SpringBootAppKindReader();
		List<SpringBootAppKind> springBootAppKindList = springBootAppKindReader.read(release.getManifest());
		AppDeployer appDeployer = this.deployerRepository.findByNameRequired(release.getPlatformName())
				.getAppDeployer();
		Map<String, String> appNameDeploymentIdMap = new HashMap<>();
		for (SpringBootAppKind springBootAppKind : springBootAppKindList) {
			String deploymentId = appDeployer.deploy(
					createAppDeploymentRequest(springBootAppKind, release.getName(),
							String.valueOf(release.getVersion())));
			appNameDeploymentIdMap.put(getApplicationName(springBootAppKind), deploymentId);
		}

		AppDeployerData appDeployerData = new AppDeployerData();
		appDeployerData.setReleaseName(release.getName());
		appDeployerData.setReleaseVersion(release.getVersion());
		appDeployerData.setDeploymentData(serializeMap(appNameDeploymentIdMap));

		this.appDeployerDataRepository.save(appDeployerData);

		// Update Status in DB
		Status status = new Status();
		status.setStatusCode(StatusCode.DEPLOYED);
		release.getInfo().setStatus(status);
		release.getInfo().setDescription("Install complete");

		// Store updated state in in DB and compute status
		return status(this.releaseRepository.save(release));
	}

	@Override
	public Release upgrade(Release existingRelease, Release replacingRelease) {

		Release release = this.releaseRepository.save(replacingRelease);

		ReleaseAnalysisReport releaseAnalysisReport = this.releaseAnalysisService.analyze(existingRelease,
				replacingRelease);
		List<String> applicationNamesToUpgrade = releaseAnalysisReport.getApplicationNamesToUpgrade();

		// Deploy the application
		SpringBootAppKindReader springBootAppKindReader = new SpringBootAppKindReader();
		List<SpringBootAppKind> springBootAppKindList = springBootAppKindReader.read(release.getManifest());
		AppDeployer appDeployer = this.deployerRepository.findByNameRequired(release.getPlatformName())
				.getAppDeployer();
		Map<String, String> appNameDeploymentIdMap = new HashMap<>();
		for (SpringBootAppKind springBootAppKind : springBootAppKindList) {
			if (applicationNamesToUpgrade.contains(getApplicationName(springBootAppKind))) {
				String deploymentId = appDeployer.deploy(
						createAppDeploymentRequest(springBootAppKind, release.getName(),
								String.valueOf(release.getVersion())));
				appNameDeploymentIdMap.put(getApplicationName(springBootAppKind), deploymentId);
			}
		}

		// Carry over the applicationDeployment information for apps that were not updated.
		AppDeployerData existingAppDeployerData = this.appDeployerDataRepository.findByReleaseNameAndReleaseVersion(
				existingRelease.getName(),
				existingRelease.getVersion());
		Map<String, String> existingAppNamesAndDeploymentIds = deserializeMap(
				existingAppDeployerData.getDeploymentData());
		for (Map.Entry<String, String> existingEntry : existingAppNamesAndDeploymentIds.entrySet()) {
			String existingName = existingEntry.getKey();
			if (!appNameDeploymentIdMap.containsKey(existingName)) {
				appNameDeploymentIdMap.put(existingName, existingEntry.getValue());
			}
		}

		AppDeployerData appDeployerData = new AppDeployerData();
		appDeployerData.setReleaseName(release.getName());
		appDeployerData.setReleaseVersion(release.getVersion());
		appDeployerData.setDeploymentData(serializeMap(appNameDeploymentIdMap));

		this.appDeployerDataRepository.save(appDeployerData);

		// Update Status in DB
		Status status = new Status();
		status.setStatusCode(StatusCode.DEPLOYED);
		release.getInfo().setStatus(status);
		release.getInfo().setDescription("Upgrade complete");

		// Store updated state in in DB and compute status
		return status(this.releaseRepository.save(release));
	}

	private String serializeMap(Map<String, String> appNameDeploymentIdMap) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(appNameDeploymentIdMap);
		}
		catch (JsonProcessingException e) {
			throw new SkipperException("Could not serialize appNameDeploymentIdMap", e);
		}
	}

	private Map<String, String> deserializeMap(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {
			};
			HashMap<String, String> result = mapper.readValue(json, typeRef);
			return result;
		}
		catch (Exception e) {
			throw new SkipperException("Could not parse appNameDeploymentIdMap JSON:" + json, e);
		}
	}

	public Release status(Release release) {
		AppDeployer appDeployer = this.deployerRepository.findByNameRequired(release.getPlatformName())
				.getAppDeployer();
		AppDeployerData appDeployerData = this.appDeployerDataRepository
				.findByReleaseNameAndReleaseVersion(release.getName(), release.getVersion());
		List<String> deploymentIds = getDeploymentIds(appDeployerData);
		if (!deploymentIds.isEmpty()) {
			boolean allDeployed = true;
			StringBuffer releaseStatusMsg = new StringBuffer();
			for (String deploymentId : deploymentIds) {
				AppStatus appStatus = appDeployer.status(deploymentId);
				if (appStatus.getState() != DeploymentState.deployed) {
					StringBuffer statusMsg = new StringBuffer(deploymentId + "=[");
					allDeployed = false;
					for (AppInstanceStatus appInstanceStatus : appStatus.getInstances().values()) {
						statusMsg.append(appInstanceStatus.getId() + "=" + appInstanceStatus.getState());
					}
					statusMsg.append("]");
					releaseStatusMsg.append(statusMsg);
				}
			}
			if (allDeployed) {
				release.getInfo().getStatus().setPlatformStatus("All the applications are deployed successfully.");
			}
			else {
				release.getInfo().getStatus().setPlatformStatus(
						"Applications deploying... " + releaseStatusMsg.toString());
			}
		}
		return release;
	}

	private List<String> getDeploymentIds(AppDeployerData appDeployerData) {
		Map<String, String> appNameDeploymentIdMap = deserializeMap(appDeployerData.getDeploymentData());
		return appNameDeploymentIdMap.values().stream().collect(Collectors.toList());
	}

	public Release delete(Release release) {
		AppDeployer appDeployer = this.deployerRepository.findByNameRequired(release.getPlatformName())
				.getAppDeployer();

		AppDeployerData appDeployerData = this.appDeployerDataRepository
				.findByReleaseNameAndReleaseVersion(release.getName(), release.getVersion());
		List<String> deploymentIds = getDeploymentIds(appDeployerData);
		if (!deploymentIds.isEmpty()) {
			Status deletingStatus = new Status();
			deletingStatus.setStatusCode(StatusCode.DELETING);
			release.getInfo().setStatus(deletingStatus);
			this.releaseRepository.save(release);
			for (String deploymentId : deploymentIds) {
				appDeployer.undeploy(deploymentId);
			}
			Status deletedStatus = new Status();
			deletedStatus.setStatusCode(StatusCode.DELETED);
			release.getInfo().setStatus(deletedStatus);
			release.getInfo().setDescription("Delete complete");
			this.releaseRepository.save(release);
		}
		return release;
	}

	@Override
	public Release delete(Release release, List<String> applicationNamesToDelete) {

		AppDeployer appDeployer = this.deployerRepository.findByNameRequired(release.getPlatformName())
				.getAppDeployer();

		AppDeployerData appDeployerData = this.appDeployerDataRepository
				.findByReleaseNameAndReleaseVersion(release.getName(), release.getVersion());

		Map<String, String> appNamesAndDeploymentIds = deserializeMap(appDeployerData.getDeploymentData());

		Status deletingStatus = new Status();
		deletingStatus.setStatusCode(StatusCode.DELETING);
		release.getInfo().setStatus(deletingStatus);
		this.releaseRepository.save(release);

		for (Map.Entry<String, String> appNameAndDeploymentId : appNamesAndDeploymentIds.entrySet()) {
			if (applicationNamesToDelete.contains(appNameAndDeploymentId.getKey())) {
				appDeployer.undeploy(appNameAndDeploymentId.getValue());
			}
		}

		Status deletedStatus = new Status();
		deletedStatus.setStatusCode(StatusCode.DELETED);
		release.getInfo().setStatus(deletedStatus);
		release.getInfo().setDescription("Delete complete");
		this.releaseRepository.save(release);
		return release;
	}

	private String getApplicationName(SpringBootAppKind springBootAppKind) {
		Map<String, String> metadata = springBootAppKind.getMetadata();
		if (!metadata.containsKey("name")) {
			throw new SkipperException("Package template must define a 'name' property in the metadata");
		}
		return metadata.get("name");
	}

	private AppDeploymentRequest createAppDeploymentRequest(SpringBootAppKind springBootAppKind, String releaseName,
			String version) {

		SpringBootAppSpec spec = springBootAppKind.getSpec();
		Map<String, String> applicationProperties = new TreeMap<>();
		if (spec.getApplicationProperties() != null) {
			applicationProperties.putAll(spec.getApplicationProperties());
		}
		AppDefinition appDefinition = new AppDefinition(getApplicationName(springBootAppKind), applicationProperties);

		Assert.hasText(spec.getResource(), "Package template must define a resource uri");
		Resource resource = delegatingResourceLoader.getResource(spec.getResource());

		Map<String, String> deploymentProperties = new TreeMap<>();
		if (spec.getDeploymentProperties() != null) {
			deploymentProperties.putAll(spec.getDeploymentProperties());
		}
		Map<String, String> metadata = springBootAppKind.getMetadata();
		if (metadata.containsKey("count")) {
			deploymentProperties.put(AppDeployer.COUNT_PROPERTY_KEY, String.valueOf(metadata.get("count")));
		}

		deploymentProperties.put(AppDeployer.GROUP_PROPERTY_KEY, releaseName + "-v" + version);

		AppDeploymentRequest appDeploymentRequest = new AppDeploymentRequest(appDefinition, resource,
				deploymentProperties);
		return appDeploymentRequest;
	}

}
