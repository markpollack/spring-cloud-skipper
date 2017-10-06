/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.skipper.deployer;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.cloud.deployer.spi.app.AppDeployer;

/**
 * @author Mark Pollack
 */
public class Deployer {

	private String id;

	private String name;

	private String type;

	@JsonIgnore
	private AppDeployer appDeployer;

	Deployer() {
	}

	public Deployer(String name, String type, AppDeployer appDeployer) {
		this.name = name;
		this.type = type;
		this.appDeployer = appDeployer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AppDeployer getAppDeployer() {
		return appDeployer;
	}

	public void setAppDeployer(AppDeployer deployer) {
		this.appDeployer = deployer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Deployer{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", type='" + type + '\'' +
				'}';
	}
}
