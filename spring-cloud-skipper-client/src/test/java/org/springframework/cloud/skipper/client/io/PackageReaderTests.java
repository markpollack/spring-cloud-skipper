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
package org.springframework.cloud.skipper.client.io;

import java.io.IOException;

import org.junit.Test;

import org.springframework.cloud.skipper.domain.Package;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mark Pollack
 */
public class PackageReaderTests {

	@Test
	public void read() throws IOException {
		Resource resource = new ClassPathResource("/repositories/sources/test/ticktock/ticktock-1.0.0");
		PackageReader packageReader = new DefaultPackageReader();

		Package pkg = packageReader.read(resource.getFile());
		assertThat(pkg).isNotNull();
	}
}
