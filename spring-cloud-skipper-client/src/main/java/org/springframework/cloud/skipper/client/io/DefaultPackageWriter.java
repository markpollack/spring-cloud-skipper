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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import org.springframework.cloud.skipper.domain.Package;
import org.springframework.cloud.skipper.domain.PackageMetadata;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

/**
 * @author Mark Pollack
 */
public class DefaultPackageWriter implements PackageWriter {

	private static final Logger logger = LoggerFactory.getLogger(DefaultPackageWriter.class);

	private Yaml yaml;

	public DefaultPackageWriter() {
		DumperOptions dumperOptions = new DumperOptions();
		dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		dumperOptions.setPrettyFlow(true);
		this.yaml = new Yaml(dumperOptions);
	}

	@Override
	public void write(Package pkg, File file) {
		File tmpDir = createTempDirectory("skipper" + pkg.getMetadata().getName()).toFile();
		File tmpTargetZipFile = calculatePackageZipFile(pkg.getMetadata(), tmpDir);
		String packageMetadata = generatePackageMetadata(pkg.getMetadata());
		writeText(new File(tmpDir, "package.yml"), packageMetadata);
		writeText(new File(tmpDir, "values.yml"), pkg.getConfigValues().getRaw());
		File templateDir = new File(tmpDir, "templates/");
		templateDir.mkdirs();
		File templateFile = new File(templateDir, pkg.getMetadata().getName() + ".yml");
		writeText(templateFile, getDefaultTemplate());

		System.out.println("test");
	}

	private String getDefaultTemplate() {
		Resource resource = new ClassPathResource("/org/springframework/cloud/skipper/client/io/generic-template.yml");
		String genericTempateData = null;
		try {
			genericTempateData = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Can't load generic template", e);
		}
		return genericTempateData;
	}

	private void writeText(File target, String body) {
		try (OutputStream stream = new FileOutputStream(target)) {
			StreamUtils.copy(body, Charset.forName("UTF-8"), stream);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot write file " + target, e);
		}
	}

	private String generatePackageMetadata(PackageMetadata packageMetadata) {
		return yaml.dump(packageMetadata);
	}

	// TODO these methods shoudl move to some lower level package unless we want the skipper
	// server to depend on
	// client.

	private Path createTempDirectory(String rootName) {
		try {
			logger.debug("Creating temp directory with root name {}", rootName);
			Path pathToReturn = Files.createTempDirectory(rootName);
			logger.debug("Created temp directory {}", pathToReturn.toString());
			return pathToReturn;
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Could not create temp directory", e);
		}
	}

	private File calculatePackageZipFile(PackageMetadata packageMetadata, File targetPath) {
		logger.debug("Calculating zip file name for {}", packageMetadata);
		return new File(targetPath, packageMetadata.getName() + "-" + packageMetadata.getVersion() + ".zip");
	}

}
