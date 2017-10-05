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

import java.util.List;

import io.jsonwebtoken.lang.Assert;

/**
 * @author Mark Pollack
 */
public class ReleaseAnalysisReport {

	private final List<String> applicationNamesToUpgrade;

	private final ReleaseDifference releaseDifference;

	// high level description of changes. ReleaseDifference .getResourceDifferences,
	// getAppDeploymentDifferences
	// look at guava map's difference util class.

	public ReleaseAnalysisReport(List<String> applicationNamesToUpgrade, ReleaseDifference releaseDifference) {
		Assert.notNull(applicationNamesToUpgrade, "ApplicationNamesToUpgrade can not be null.");
		Assert.notNull(releaseDifference, "ReleaseDifference can not be null.");
		this.applicationNamesToUpgrade = applicationNamesToUpgrade;
		this.releaseDifference = releaseDifference;
	}

	public List<String> getApplicationNamesToUpgrade() {
		return applicationNamesToUpgrade;
	}

	public ReleaseDifference getReleaseDifference() {
		return releaseDifference;
	}
}
