package org.springframework.cloud.skipper.server.domain;

public class AppDeploymentKind {

	private String kind;

	private Deployment deployment;

	public AppDeploymentKind() {
	}

	public AppDeploymentKind(String kind, Deployment deployment) {

		this.kind = kind;
		this.deployment = deployment;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public Deployment getDeployment() {
		return deployment;
	}

	public void setDeployment(Deployment deployment) {
		this.deployment = deployment;
	}
}
