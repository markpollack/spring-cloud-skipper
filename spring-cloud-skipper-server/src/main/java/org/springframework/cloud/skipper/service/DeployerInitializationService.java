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
package org.springframework.cloud.skipper.service;

import java.time.Duration;
import java.util.Map;

import com.github.zafarkhaja.semver.Version;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.info.GetInfoRequest;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryAppDeployer;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryAppNameGenerator;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryConnectionProperties;
import org.springframework.cloud.deployer.spi.cloudfoundry.CloudFoundryDeploymentProperties;
import org.springframework.cloud.deployer.spi.core.RuntimeEnvironmentInfo;
import org.springframework.cloud.deployer.spi.kubernetes.ContainerFactory;
import org.springframework.cloud.deployer.spi.kubernetes.DefaultContainerFactory;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesAppDeployer;
import org.springframework.cloud.deployer.spi.kubernetes.KubernetesDeployerProperties;
import org.springframework.cloud.deployer.spi.local.LocalAppDeployer;
import org.springframework.cloud.deployer.spi.local.LocalDeployerProperties;
import org.springframework.cloud.deployer.spi.util.RuntimeVersionUtils;
import org.springframework.cloud.skipper.config.CloudFoundryPlatformProperties;
import org.springframework.cloud.skipper.config.KubernetesPlatformProperties;
import org.springframework.cloud.skipper.config.LocalPlatformProperties;
import org.springframework.cloud.skipper.deployer.Deployer;
import org.springframework.cloud.skipper.repository.DeployerRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Populates the DeployerRepository with AppDeployer instances
 *
 * @author Mark Pollack
 * @author Ilayaperumal Gopinathan
 */
@Service
public class DeployerInitializationService {

	private final Logger logger = LoggerFactory.getLogger(DeployerInitializationService.class);

	private final LocalPlatformProperties localPlatformProperties;

	private final CloudFoundryPlatformProperties cloudFoundryPlatformProperties;

	private final KubernetesPlatformProperties kubernetesPlatformProperties;

	private DeployerRepository deployerRepository;

	@Autowired
	public DeployerInitializationService(DeployerRepository deployerRepository,
			LocalPlatformProperties localPlatformProperties,
			CloudFoundryPlatformProperties cloudFoundryPlatformProperties,
			KubernetesPlatformProperties kubernetesPlatformProperties) {
		this.deployerRepository = deployerRepository;
		this.localPlatformProperties = localPlatformProperties;
		this.cloudFoundryPlatformProperties = cloudFoundryPlatformProperties;
		this.kubernetesPlatformProperties = kubernetesPlatformProperties;
	}

	@EventListener
	public void initialize(ApplicationReadyEvent event) {
		createAndSaveLocalAppDeployers();
		createAndSaveCFAppDeployers();
		createAndSaveKubernetesAppDeployers();
	}

	protected void createAndSaveLocalAppDeployers() {
		Map<String, LocalDeployerProperties> localDeployerPropertiesMap = localPlatformProperties.getAccounts();
		if (localDeployerPropertiesMap.isEmpty()) {
			logger.info(
					"No local platform deployers defined, adding local deployer named 'default' into localPlatformProperties.");
			localDeployerPropertiesMap.put("default", new LocalDeployerProperties());
		}
		for (Map.Entry<String, LocalDeployerProperties> entry : localDeployerPropertiesMap
				.entrySet()) {
			LocalAppDeployer localAppDeployer = new LocalAppDeployer(entry.getValue());
			Deployer deployer = new Deployer(entry.getKey(), "local", localAppDeployer);
			deployerRepository.save(deployer);
			logger.info("Added Local Deployer account " + entry.getKey() + " into Deployer Repository.");
		}
	}

	protected void createAndSaveCFAppDeployers() {
		Map<String, CloudFoundryPlatformProperties.CloudFoundryProperties> cfConnectionProperties = cloudFoundryPlatformProperties
				.getAccounts();
		for (Map.Entry<String, CloudFoundryPlatformProperties.CloudFoundryProperties> entry : cfConnectionProperties
				.entrySet()) {
			CloudFoundryAppNameGenerator appNameGenerator = new CloudFoundryAppNameGenerator(
					entry.getValue().getDeployment());
			CloudFoundryDeploymentProperties deploymentProperties = entry.getValue().getDeployment();
			if (deploymentProperties == null) {
				// todo: use server level shared deployment properties
				deploymentProperties = new CloudFoundryDeploymentProperties();
			}
			CloudFoundryConnectionProperties connectionProperties = entry.getValue().getConnection();
			try {
				ConnectionContext connectionContext = DefaultConnectionContext.builder()
						.apiHost(connectionProperties.getUrl().getHost())
						.skipSslValidation(connectionProperties.isSkipSslValidation())
						.build();
				TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
						.username(connectionProperties.getUsername())
						.password(connectionProperties.getPassword())
						.build();
				CloudFoundryClient cloudFoundryClient = ReactorCloudFoundryClient.builder()
						.connectionContext(connectionContext)
						.tokenProvider(tokenProvider)
						.build();
				Version version = cloudFoundryClient.info()
						.get(GetInfoRequest.builder()
								.build())
						.map(response -> Version.valueOf(response.getApiVersion()))
						.doOnNext(
								versionInfo -> logger
										.info("Connecting to Cloud Foundry with API Version {}", versionInfo))
						.block(Duration.ofSeconds(deploymentProperties.getApiTimeout()));
				RuntimeEnvironmentInfo runtimeEnvironmentInfo = new RuntimeEnvironmentInfo.Builder()
						.implementationName(CloudFoundryAppDeployer.class.getSimpleName())
						.spiClass(AppDeployer.class)
						.implementationVersion(RuntimeVersionUtils.getVersion(CloudFoundryAppDeployer.class))
						.platformType("Cloud Foundry")
						.platformClientVersion(RuntimeVersionUtils.getVersion(cloudFoundryClient.getClass()))
						.platformApiVersion(version.toString())
						.platformHostVersion("unknown")
						.addPlatformSpecificInfo("API Endpoint", connectionProperties.getUrl().toString())
						.build();
				CloudFoundryOperations cloudFoundryOperations = DefaultCloudFoundryOperations.builder()
						.cloudFoundryClient(cloudFoundryClient)
						.organization(connectionProperties.getOrg())
						.space(connectionProperties.getSpace())
						.build();
				CloudFoundryAppDeployer cfAppDeployer = new CloudFoundryAppDeployer(appNameGenerator,
						deploymentProperties,
						cloudFoundryOperations, runtimeEnvironmentInfo);
				Deployer deployer = new Deployer(entry.getKey(), "cloudfoundry", cfAppDeployer);
				deployerRepository.save(deployer);
				logger.info("Adding CF Deployer account " + entry.getKey() + " into Deployer Repository.");
			}
			catch (Exception e) {
				logger.warn("CloudFoundry Deployer account" + entry.getKey() + " could not be added." + e.getMessage());
			}
		}
	}

	protected void createAndSaveKubernetesAppDeployers() {
		Map<String, KubernetesDeployerProperties> kubernetesDeployerPropertiesMap = this.kubernetesPlatformProperties
				.getAccounts();
		for (Map.Entry<String, KubernetesDeployerProperties> entry : kubernetesDeployerPropertiesMap.entrySet()) {
			KubernetesDeployerProperties properties = entry.getValue();
			KubernetesClient kubernetesClient = new DefaultKubernetesClient().inNamespace(properties.getNamespace());
			ContainerFactory containerFactory = new DefaultContainerFactory(properties);
			KubernetesAppDeployer kubernetesAppDeployer = new KubernetesAppDeployer(properties, kubernetesClient,
					containerFactory);
			Deployer deployer = new Deployer(entry.getKey(), "kubernetes", kubernetesAppDeployer);
			this.deployerRepository.save(deployer);
			logger.info("Added Kubernetes Deployer account " + entry.getKey() + " into Deployer Repository.");
		}
	}
}
