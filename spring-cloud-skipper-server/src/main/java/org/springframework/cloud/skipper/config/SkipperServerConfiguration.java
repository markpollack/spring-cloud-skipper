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
package org.springframework.cloud.skipper.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.deployer.resource.docker.DockerResourceLoader;
import org.springframework.cloud.deployer.resource.maven.MavenResourceLoader;
import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.cloud.skipper.deployer.AppDeployerDataRepository;
import org.springframework.cloud.skipper.deployer.AppDeployerReleaseManager;
import org.springframework.cloud.skipper.deployer.AppDeploymentRequestFactory;
import org.springframework.cloud.skipper.deployer.ReleaseAnalysisService;
import org.springframework.cloud.skipper.io.DefaultPackageReader;
import org.springframework.cloud.skipper.io.DefaultPackageWriter;
import org.springframework.cloud.skipper.io.PackageReader;
import org.springframework.cloud.skipper.io.PackageWriter;
import org.springframework.cloud.skipper.repository.DeployerRepository;
import org.springframework.cloud.skipper.repository.PackageMetadataRepository;
import org.springframework.cloud.skipper.repository.ReleaseRepository;
import org.springframework.cloud.skipper.repository.RepositoryRepository;
import org.springframework.cloud.skipper.repository.SimpleDeployerRepository;
import org.springframework.cloud.skipper.service.PackageService;
import org.springframework.cloud.skipper.service.ReleaseManager;
import org.springframework.cloud.skipper.service.ReleaseService;
import org.springframework.cloud.skipper.service.ReleaseStateUpdateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

/**
 * Main configuration class for the server.
 *
 * @author Mark Pollack
 * @author Ilayaperumal Gopinathan
 */
@Configuration
@EnableConfigurationProperties({ SkipperServerProperties.class, CloudFoundryPlatformProperties.class,
		LocalPlatformProperties.class, KubernetesPlatformProperties.class,
		MavenConfigurationProperties.class })
public class SkipperServerConfiguration {

	@Autowired
	private ReleaseRepository releaseRepository;

	@Bean
	public ReleaseService releaseService(PackageMetadataRepository packageMetadataRepository,
			ReleaseRepository releaseRepository,
			PackageService packageService,
			ReleaseManager releaseManager,
			DeployerRepository deployerRepository,
			ReleaseAnalysisService releaseAnalysisService) {
		return new ReleaseService(packageMetadataRepository, releaseRepository, packageService,
				releaseManager, deployerRepository, releaseAnalysisService);

	}

	@Bean
	public ReleaseManager appDeployerReleaseManager(ReleaseRepository releaseRepository,
			AppDeployerDataRepository appDeployerDataRepository,
			DeployerRepository deployerRepository,
			ReleaseAnalysisService releaseAnalysisService,
			AppDeploymentRequestFactory appDeploymentRequestFactory) {
		return new AppDeployerReleaseManager(releaseRepository, appDeployerDataRepository,
				deployerRepository, releaseAnalysisService, appDeploymentRequestFactory);
	}

	@Bean
	public PackageService packageService(RepositoryRepository repositoryRepository,
			PackageMetadataRepository packageMetadataRepository,
			PackageReader packageReader) {
		return new PackageService(repositoryRepository, packageMetadataRepository, packageReader);

	}

	@Bean
	public DeployerRepository deployerRepository() {
		return new SimpleDeployerRepository();
	}

	@Bean
	public ReleaseAnalysisService releaseAnalysisService() {
		return new ReleaseAnalysisService();
	}

	@Bean
	public AppDeploymentRequestFactory appDeploymentRequestFactory(DelegatingResourceLoader delegatingResourceLoader) {
		return new AppDeploymentRequestFactory(delegatingResourceLoader);
	}

	@Bean
	@ConditionalOnProperty("skipper.scheduling.enabled")
	public ReleaseStateUpdateService releaseStateUpdateService(ReleaseService releaseService,
			DeployerRepository deployerRepository) {
		return new ReleaseStateUpdateService(releaseService, this.releaseRepository, deployerRepository);
	}

	@Bean
	public DelegatingResourceLoader delegatingResourceLoader(MavenConfigurationProperties mavenProperties) {
		DockerResourceLoader dockerLoader = new DockerResourceLoader();
		MavenResourceLoader mavenResourceLoader = new MavenResourceLoader(mavenProperties);
		Map<String, ResourceLoader> loaders = new HashMap<>();
		loaders.put("docker", dockerLoader);
		loaders.put("maven", mavenResourceLoader);
		return new DelegatingResourceLoader(loaders);
	}

	@Bean
	public PackageReader packageReader() {
		return new DefaultPackageReader();
	}

	@Bean
	public PackageWriter packageWriter() {
		return new DefaultPackageWriter();
	}

}
