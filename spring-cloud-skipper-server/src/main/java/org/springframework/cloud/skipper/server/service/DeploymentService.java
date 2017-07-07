package org.springframework.cloud.skipper.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.app.AppStatus;
import org.springframework.cloud.deployer.spi.app.DeploymentState;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.server.domain.AppDeploymentKind;
import org.springframework.cloud.skipper.server.domain.Deployment;
import org.springframework.cloud.skipper.server.repository.ReleaseRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DeploymentService {

	private final static Logger log = LoggerFactory
			.getLogger(DeploymentService.class);
	private AppDeployer appDeployer;
	private DelegatingResourceLoader delegatingResourceLoader;
	private ReleaseRepository releaseRepository;

	public DeploymentService(AppDeployer appDeployer,
			DelegatingResourceLoader delegatingResourceLoader,
			ReleaseRepository releaseRepository) {
		this.appDeployer = appDeployer;
		this.delegatingResourceLoader = delegatingResourceLoader;
		this.releaseRepository = releaseRepository;
	}

	public void deploy(Release release) {
		List<Deployment> appDeployments = unmarshallDeployments(release.getManifest());

		List<String> deploymentIds = new ArrayList<>();
		for (Deployment appDeployment : appDeployments) {
			deploymentIds.add(appDeployer.deploy(
					createAppDeploymentRequest(appDeployment, release.getName(),
							String.valueOf(release.getVersion()))));
		}
		release.setDeploymentId(StringUtils.collectionToCommaDelimitedString(deploymentIds));

		// TODO update status in DB.

	}

	public void undeploy(Release release) {
		List<String> deploymentIds = Arrays
				.asList(StringUtils.commaDelimitedListToStringArray(release.getDeploymentId()));

		for (String deploymentId : deploymentIds) {
			appDeployer.undeploy(deploymentId);
		}
		// TODO update status

		releaseRepository.save(release);
	}

	public boolean isHealthy(Release release) {
		boolean isHealthy = false;
		long timeout = System.currentTimeMillis() + 60000;
		while (!isHealthy && System.currentTimeMillis() < timeout) {
			List<String> deploymentIds = Arrays
					.asList(StringUtils.commaDelimitedListToStringArray(release.getDeploymentId()));
			for (String deploymentId : deploymentIds) {
				AppStatus status = appDeployer.status(deploymentId);
				log.info("Deployement ID + " + deploymentId + ", status = " + status.getState());
				if (status.getState() == DeploymentState.deployed) {
					isHealthy = true;
				}
			}
			try {
				Thread.sleep(2000);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		return isHealthy;
	}

	private AppDeploymentRequest createAppDeploymentRequest(Deployment deployment, String releaseName,
			String version) {

		AppDefinition appDefinition = new AppDefinition(deployment.getName(), deployment.getApplicationProperties());
		Resource resource = delegatingResourceLoader.getResource(deployment.getResource());

		Map<String, String> deploymentProperties = deployment.getDeploymentProperties();
		deploymentProperties.put(AppDeployer.COUNT_PROPERTY_KEY, String.valueOf(deployment.getCount()));
		deploymentProperties.put(AppDeployer.GROUP_PROPERTY_KEY, releaseName + "-v" + version);

		AppDeploymentRequest appDeploymentRequest = new AppDeploymentRequest(appDefinition, resource,
				deploymentProperties);
		return appDeploymentRequest;
	}

	private List<Deployment> unmarshallDeployments(String manifests) {

		List<AppDeploymentKind> deploymentKindList = new ArrayList<>();
		YAMLMapper mapper = new YAMLMapper();
		try {
			MappingIterator<AppDeploymentKind> it = mapper.readerFor(AppDeploymentKind.class).readValues(manifests);
			while (it.hasNextValue()) {
				AppDeploymentKind deploymentKind = it.next();
				deploymentKindList.add(deploymentKind);
			}

		}
		catch (IOException e) {
			throw new IllegalArgumentException("Can't parse Package's manifest YAML", e);
		}

		List<Deployment> deploymentList = deploymentKindList.stream().map(AppDeploymentKind::getDeployment)
				.collect(Collectors.toList());
		return deploymentList;
	}
}
