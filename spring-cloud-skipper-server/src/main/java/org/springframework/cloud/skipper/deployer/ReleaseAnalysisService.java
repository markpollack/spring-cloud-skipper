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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.skipper.domain.Package;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.domain.SpringBootAppKind;
import org.springframework.cloud.skipper.domain.SpringBootAppKindReader;
import org.springframework.stereotype.Service;

/**
 * Analyze the new release manifest and the previous one to determine the minimum number
 * of releases to install and delete when upgrading.
 * @author Mark Pollack
 */
@Service
public class ReleaseAnalysisService {
	private final Logger logger = LoggerFactory.getLogger(ReleaseAnalysisService.class);

	public ReleaseAnalysisReport analyze(Release existingRelease, Release replacingRelease) {

		// For now, assume single package with no deps or package with same number of deps

		if (existingRelease.getPkg().getDependencies().size() == replacingRelease.getPkg().getDependencies().size()) {
			if (existingRelease.getPkg().getDependencies().size() == 0) {
				List<SpringBootAppKind> existingSpringBootAppKindList = SpringBootAppKindReader
						.read(existingRelease.getManifest());
				List<SpringBootAppKind> replacingSpringBootAppKindList = SpringBootAppKindReader
						.read(replacingRelease.getManifest());
				ReleaseDifference difference = compare(existingSpringBootAppKindList.get(0),
						replacingSpringBootAppKindList.get(0));
				List<String> appsToDelete = new ArrayList<>();
				if (!difference.areEqual()) {
					appsToDelete.add(existingSpringBootAppKindList.get(0).getApplicationName().trim());
				}
				return new ReleaseAnalysisReport(appsToDelete, difference);
			}
		}

		List<String> appsToDelete = new ArrayList<>();
		if (existingRelease.getPkg().getMetadata().getName().equals("ticktock")) {
			appsToDelete.add("log");
		}
		else {
			appsToDelete.add(replacingRelease.getPkg().getMetadata().getName());
			for (Package dependentPackage : replacingRelease.getPkg().getDependencies()) {
				appsToDelete.add(dependentPackage.getMetadata().getName());
			}
		}

		logger.info("Apps to delete " + Arrays.toString(appsToDelete.toArray()));
		ReleaseAnalysisReport report = new ReleaseAnalysisReport(appsToDelete, null);
		return report;
	}

	private ReleaseDifference compare(SpringBootAppKind existingSpringBootAppKind,
			SpringBootAppKind replacingSpringBootAppKind) {

		// application name ame must be equal??
		// String existingAppName = existingSpringBootAppKind.getApplicationName().trim();
		// String replacingAppName = replacingSpringBootAppKind.getApplicationName().trim();
		// if (!existingAppName.equals(replacingAppName)) {
		// String difference = String.format("Existing application name =[%s], Replacing
		// application name=[%s]",
		// existingAppName,
		// replacingAppName);
		// return new ReleaseDifference(false, difference);
		// }

		String existingResource = existingSpringBootAppKind.getSpec().getResource().trim();
		String replacingResource = replacingSpringBootAppKind.getSpec().getResource().trim();
		if (!existingResource.equals(replacingResource)) {
			String difference = String.format("Existing resource =[%s], Replacing name=[%s]",
					existingResource, replacingResource);
			return new ReleaseDifference(false, difference);
		}

		// Compare Application Properties

		// Compare Deployment Prioperties

		return new ReleaseDifference(true);

	}
}
