package org.springframework.cloud.skipper.server.updaters;

import org.springframework.cloud.skipper.domain.Release;

public interface UpdateStrategy {
	Release update(Release currentRelease, Release updatedRelease);
}
