package org.springframework.cloud.skipper.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.domain.SkipperPackage;
import org.springframework.cloud.skipper.server.repository.ReleaseRepository;
import org.springframework.stereotype.Service;

/**
 * @author Mark Pollack
 */
@Service
public class ReleaseService {

	private final ReleaseRepository releaseRepository;

	@Autowired
	public ReleaseService(ReleaseRepository releaseRepository) {
		this.releaseRepository = releaseRepository;
	}

	public Release install(Release release, SkipperPackage skipperPackage) {

		releaseRepository.save(release);

		// deploy

		return release;

	}
}
