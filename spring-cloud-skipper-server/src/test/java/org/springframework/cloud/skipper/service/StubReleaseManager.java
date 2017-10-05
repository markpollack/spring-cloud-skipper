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

import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.cloud.skipper.deployer.AppDeployerDataRepository;
import org.springframework.cloud.skipper.deployer.AppDeployerReleaseManager;
import org.springframework.cloud.skipper.repository.DeployerRepository;
import org.springframework.cloud.skipper.repository.ReleaseRepository;

/**
 * @author Mark Pollack
 */
public class StubReleaseManager extends AppDeployerReleaseManager {

	public StubReleaseManager(ReleaseRepository releaseRepository, AppDeployerDataRepository appDeployerDataRepository,
			DelegatingResourceLoader delegatingResourceLoader, DeployerRepository deployerRepository,
			ReleaseAnalysisService releaseAnalysisService) {
		super(releaseRepository, appDeployerDataRepository, delegatingResourceLoader, deployerRepository,
				releaseAnalysisService);
	}

}
