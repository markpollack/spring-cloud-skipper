package org.springframework.cloud.skipper.client;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("skipper.client")
public class SkipperClientConfigurationProperties {

	private String home = System.getProperty("user.home") + File.separator + ".skipper";

	private String host = "http://localhost:8080/api";

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
