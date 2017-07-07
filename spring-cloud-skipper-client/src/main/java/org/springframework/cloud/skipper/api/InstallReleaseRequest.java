package org.springframework.cloud.skipper.api;

import org.springframework.cloud.skipper.domain.SkipperPackage;

public class InstallReleaseRequest {

	private SkipperPackage skipperPackage;

	private String name;

	public InstallReleaseRequest() {
	}

	public InstallReleaseRequest(String releaseName, SkipperPackage skipperPackage) {
		this.name = releaseName;
		this.skipperPackage = skipperPackage;
	}

	public SkipperPackage getSkipperPackage() {
		return skipperPackage;
	}

	public void setSkipperPackage(SkipperPackage skipperPackage) {
		this.skipperPackage = skipperPackage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
