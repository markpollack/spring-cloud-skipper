package org.springframework.cloud.skipper.api;

import org.springframework.cloud.skipper.domain.SkipperPackage;

/**
 * @author Mark Pollack
 */
public class UpdateReleaseRequest {

	private SkipperPackage skipperPackage;

	private String name;

	public UpdateReleaseRequest() {
	}

	public UpdateReleaseRequest(String name, SkipperPackage skipperPackage) {
		this.skipperPackage = skipperPackage;
		this.name = name;
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
