package org.springframework.cloud.skipper.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * @author Mark Pollack
 */
@RedisHash("releases")
public class Release {

	@Id
	private String id;

	// links the id returned from the app deployer to this release
	private String deploymentId;

	private String name;

	private SkipperPackage skipperPackage;

	private String manifest;

	private int version;

	public Release() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SkipperPackage getSkipperPackage() {
		return skipperPackage;
	}

	public void setSkipperPackage(SkipperPackage skipperPackage) {
		this.skipperPackage = skipperPackage;
	}

	public String getManifest() {
		return manifest;
	}

	public void setManifest(String manifest) {
		this.manifest = manifest;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
