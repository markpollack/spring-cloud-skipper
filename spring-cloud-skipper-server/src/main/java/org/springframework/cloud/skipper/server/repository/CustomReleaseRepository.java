package org.springframework.cloud.skipper.server.repository;

import org.springframework.cloud.skipper.domain.Release;

/**
 * @author Mark Pollack
 */
public interface CustomReleaseRepository {

	Release findLatestRelease(String releaseName);

	Release findByNameAndVersion(String releaseName, int version);
}
