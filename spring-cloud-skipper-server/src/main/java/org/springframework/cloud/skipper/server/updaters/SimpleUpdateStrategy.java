package org.springframework.cloud.skipper.server.updaters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.server.repository.ManifestRepository;
import org.springframework.cloud.skipper.server.service.DeploymentService;
import org.springframework.stereotype.Component;

@Component
public class SimpleUpdateStrategy implements UpdateStrategy {

	private final DeploymentService deploymentService;

	private final ManifestRepository manifestRepository;

	@Autowired
	public SimpleUpdateStrategy(DeploymentService deploymentService, ManifestRepository manifestRepository) {
		this.deploymentService = deploymentService;
		this.manifestRepository = manifestRepository;
	}

	@Override
	public Release update(Release currentRelease, Release updatedRelease) {
		deploymentService.deploy(updatedRelease);

		// TODO Do something fancy in terms of health detection of new release.

		manifestRepository.save(updatedRelease);

		deploymentService.undeploy(currentRelease);

		// releaseDeployer.calculateStatus(updatedRelease);

		return updatedRelease;
	}
}
