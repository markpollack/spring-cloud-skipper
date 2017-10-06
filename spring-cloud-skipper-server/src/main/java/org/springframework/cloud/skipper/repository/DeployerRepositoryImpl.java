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

import org.springframework.cloud.skipper.deployer.Deployer;

/**
 * @author Mark Pollack
 */
public class DeployerRepositoryImpl implements DeployerRepositoryCustom {

	// @Autowired
	private DeployerRepository deployerRepository;

	@Override
	public Deployer findByNameRequired(String name) {
		return null;
		// Deployer deployer = deployerRepository.findByName(name);
		// if (deployer == null) {
		// List<Deployer> list = StreamSupport
		// .stream(deployerRepository.findAll().spliterator(), false)
		// .collect(Collectors.toList());
		// throw new SkipperException(String.format("No deployer named '%s' in DeployerRepository.
		// " +
		// "Existing deployers %s.", name, Arrays.toString(list.toArray())));
		// }
		// return deployer;
	}
}
