package org.springframework.cloud.skipper.api;

import org.springframework.cloud.skipper.domain.Release;

public class HistoryResponse {


	private Release[] releases;

	public HistoryResponse() {
	}

	public Release[] getReleases() {
		return releases;
	}

	public void setReleases(Release[] releases) {
		this.releases = releases;
	}
}
