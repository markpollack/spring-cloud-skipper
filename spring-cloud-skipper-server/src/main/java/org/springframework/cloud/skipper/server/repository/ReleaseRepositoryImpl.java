package org.springframework.cloud.skipper.server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.domain.Release;

/**
 * @author Mark Pollack
 */
public class ReleaseRepositoryImpl implements CustomReleaseRepository {

	@Autowired
	private ReleaseRepository releaseRepository;

	@Override
	public Release findLatestRelease(String releaseName) {
		Iterable<Release> releases = releaseRepository.findAll();
		int lastVersion = 0;
		Release latestRelease = null;
		for (Release release : releases) {
			// Find the latest release
			if (release.getName().equals(releaseName)) {
				if (release.getVersion() > lastVersion) {
					lastVersion = release.getVersion();
					latestRelease = release;
				}
			}
		}
		return latestRelease;
	}
}
