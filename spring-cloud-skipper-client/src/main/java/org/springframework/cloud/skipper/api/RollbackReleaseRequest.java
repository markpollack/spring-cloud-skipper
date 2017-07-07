package org.springframework.cloud.skipper.api;

/**
 * @author Mark Pollack
 */
public class RollbackReleaseRequest {

	private String name;

	private int version;

	public RollbackReleaseRequest() {
	}

	public RollbackReleaseRequest(String name, int version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
