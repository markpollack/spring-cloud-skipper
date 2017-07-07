package org.springframework.cloud.skipper.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.domain.SkipperPackage;
import org.springframework.cloud.skipper.domain.Template;
import org.springframework.cloud.skipper.server.repository.ReleaseRepository;
import org.springframework.stereotype.Service;

/**
 * @author Mark Pollack
 */
@Service
public class ReleaseService {

	private final ReleaseRepository releaseRepository;

	private final DeploymentService deploymentService;

	@Autowired
	public ReleaseService(ReleaseRepository releaseRepository, DeploymentService deploymentService) {
		this.releaseRepository = releaseRepository;
		this.deploymentService = deploymentService;
	}

	public Release install(Release release, SkipperPackage skipperPackage) {
		release.setManifest(createManifest(skipperPackage));
		releaseRepository.save(release);
		deploymentService.deploy(release);
		return release;
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
