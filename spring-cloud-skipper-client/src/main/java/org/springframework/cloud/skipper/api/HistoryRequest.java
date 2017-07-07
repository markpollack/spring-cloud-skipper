package org.springframework.cloud.skipper.api;

/**
 * @author Mark Pollack
 */
public class HistoryRequest {

	private String name;

	public HistoryRequest() {
	}

	public HistoryRequest(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
