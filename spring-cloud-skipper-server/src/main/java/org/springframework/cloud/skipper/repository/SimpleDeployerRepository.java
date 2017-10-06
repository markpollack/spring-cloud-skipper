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
package org.springframework.cloud.skipper.repository;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.skipper.SkipperException;
import org.springframework.cloud.skipper.deployer.Deployer;
import org.springframework.util.Assert;

/**
 * @author Mark Pollack
 */
public class SimpleDeployerRepository implements DeployerRepository {

	private static final Logger log = LoggerFactory.getLogger(SimpleDeployerRepository.class);

	private Map<String, Deployer> deployerMap = new TreeMap<>();

	public SimpleDeployerRepository() {
		log.info("**** CREATING A NEW SimpleDeployerRepository");
	}

	@Override
	public Deployer findByName(String name) {
		if (deployerMap.containsKey(name)) {
			return deployerMap.get(name);
		}
		else {
			return null;
		}
	}

	@Override
	public Deployer findByNameRequired(String name) {
		if (deployerMap.containsKey(name)) {
			return deployerMap.get(name);
		}
		else {
			throw new SkipperException("Deployer with name " + name + " does not exist.");
		}
	}

	@Override
	public Deployer save(Deployer deployer) {
		deployer.setId(UUID.randomUUID().toString());
		Assert.notNull(deployer, "Deployer instance can not be null");
		deployerMap.put(deployer.getName(), deployer);
		return deployer;
	}

	@Override
	public List<Deployer> findAll() {
		return deployerMap.values().stream().collect(Collectors.toList());
	}

	@Override
	public int count() {
		return deployerMap.size();
	}
}
