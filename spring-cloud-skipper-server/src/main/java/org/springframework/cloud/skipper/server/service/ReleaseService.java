package org.springframework.cloud.skipper.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.domain.SkipperPackage;
import org.springframework.cloud.skipper.domain.Template;
import org.springframework.cloud.skipper.server.repository.ManifestRepository;
import org.springframework.cloud.skipper.server.repository.ReleaseRepository;
import org.springframework.cloud.skipper.server.updaters.UpdateStrategy;
import org.springframework.stereotype.Service;

/**
 * @author Mark Pollack
 */
@Service
public class ReleaseService {

	private final ReleaseRepository releaseRepository;

	private final DeploymentService deploymentService;

	private final UpdateStrategy updateStrategy;

	private final ManifestRepository manifestRepository;

	@Autowired
	public ReleaseService(ReleaseRepository releaseRepository,
			DeploymentService deploymentService,
			UpdateStrategy updateStrategy,
			ManifestRepository manifestRepository) {
		this.releaseRepository = releaseRepository;
		this.deploymentService = deploymentService;
		this.updateStrategy = updateStrategy;
		this.manifestRepository = manifestRepository;
	}

	public Release install(Release release, SkipperPackage skipperPackage) {
		release.setManifest(createManifest(skipperPackage));
		releaseRepository.save(release);
		deploymentService.deploy(release);
		manifestRepository.save(release);

		//TODO calculate status

		return release;
	}

	public synchronized Release rollback(String name, int version) {

		Release currentRelease = releaseRepository.findLatestRelease(name);
		int rollbackVersion = version;
		// Go back by one if no version specified
		if (version == 0) {
			rollbackVersion = currentRelease.getVersion() - 1;
		}
		Release previousRelease = releaseRepository.findByNameAndVersion(name, version);

		Release newRelease = new Release();
		newRelease.setName(name);
		newRelease.setSkipperPackage(previousRelease.getSkipperPackage());
		newRelease.setManifest(previousRelease.getManifest());
		newRelease.setVersion(currentRelease.getVersion() + 1);

		releaseRepository.save(newRelease);

		return updateStrategy.update(currentRelease, newRelease);
	}

	public synchronized Release update(String name, SkipperPackage skipperPackage) {

		Release currentRelease = releaseRepository.findLatestRelease(name);
		int revision = currentRelease.getVersion() + 1;
		String manifest = createManifest(skipperPackage);

		Release updatedRelease = new Release();
		updatedRelease.setName(name);
		updatedRelease.setSkipperPackage(skipperPackage);
		updatedRelease.setVersion(revision);
		updatedRelease.setManifest(manifest);

		releaseRepository.save(updatedRelease);

		return updateStrategy.update(currentRelease, updatedRelease);

	}

	private String createManifest(SkipperPackage skipperPackage) {

		// Aggregate all valid manifests into one big doc.
		StringBuilder sb = new StringBuilder();

		Template[] templates = skipperPackage.getTemplates();
		if (templates != null) {
			for (Template template : templates) {
				sb.append("\n---\n# Source: " + template.getName() + "\n");
				sb.append(template.getData());
			}
		}

		if (skipperPackage.getDependencies() != null) {
			SkipperPackage[] packages = skipperPackage.getDependencies();
			for (SkipperPackage subPackage : packages) {
				sb.append(createManifest(subPackage));
			}
		}

		return sb.toString();
	}


}
