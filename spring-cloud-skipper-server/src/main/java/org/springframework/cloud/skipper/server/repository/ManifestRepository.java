package org.springframework.cloud.skipper.server.repository;

import org.springframework.cloud.skipper.domain.Release;

public interface ManifestRepository {

	void save(Release release);
}
