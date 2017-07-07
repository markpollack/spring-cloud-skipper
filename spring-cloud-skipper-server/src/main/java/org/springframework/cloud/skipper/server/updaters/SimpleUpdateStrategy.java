package org.springframework.cloud.skipper.server.updaters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.server.repository.ManifestRepository;
import org.springframework.cloud.skipper.server.service.DeploymentService;
import org.springframework.stereotype.Component;

@Component
public class SimpleUpdateStrategy implements UpdateStrategy {

	private final DeploymentService deploymentService;

	private final ManifestRepository manifestRepository;

	private final static Logger log = LoggerFactory.getLogger(SimpleUpdateStrategy.class);

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



		deploymentService.isHealthy(updatedRelease);


		log.info("Undeploying current release" + currentRelease);
		deploymentService.undeploy(currentRelease);

		// releaseDeployer.calculateStatus(updatedRelease);

		return updatedRelease;
	}
}
