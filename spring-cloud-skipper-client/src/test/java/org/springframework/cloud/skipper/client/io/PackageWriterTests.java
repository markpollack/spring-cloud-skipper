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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import org.springframework.cloud.skipper.domain.ConfigValues;
import org.springframework.cloud.skipper.domain.Package;
import org.springframework.cloud.skipper.domain.PackageMetadata;
import org.springframework.cloud.skipper.domain.Template;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

/**
 * @author Mark Pollack
 */
public class PackageWriterTests {

	@Test
	public void test() throws IOException {
		PackageWriter packageWriter = new DefaultPackageWriter();
		Package pkgtoWrite = createSimplePackage();
		PackageMetadata packageMetadata = pkgtoWrite.getMetadata();

		Path tempPath = Files.createTempDirectory("tests");
		File outputfile = new File(tempPath.toFile(),
				packageMetadata.getName() + "-" + packageMetadata.getVersion() + ".zip");

		packageWriter.write(pkgtoWrite, outputfile);
	}

	private Package createSimplePackage() throws IOException {
		Package pkg = new Package();

		// Add package metadata
		PackageMetadata packageMetadata = new PackageMetadata();
		packageMetadata.setName("myapp");
		packageMetadata.setVersion("1.0.0");
		packageMetadata.setMaintainer("bob");
		pkg.setMetadata(packageMetadata);

		// Add ConfigValues
		Map map = new HashMap<String, String>();
		map.put("foo", "bar");
		map.put("fiz", "faz");
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		dumperOptions.setPrettyFlow(true);
		Yaml yaml = new Yaml(dumperOptions);
		ConfigValues configValues = new ConfigValues();
		configValues.setRaw(yaml.dump(map));
		pkg.setConfigValues(configValues);

		// Add template
		Resource resource = new ClassPathResource("/org/springframework/cloud/skipper/client/io/generic-template.yml");
		String genericTempateData = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
		Template template = new Template();
		template.setData(genericTempateData);
		template.setName(resource.getURL().toString());
		List<Template> templateList = new ArrayList<>();
		templateList.add(template);
		pkg.setTemplates(templateList);

		return pkg;
	}
}
