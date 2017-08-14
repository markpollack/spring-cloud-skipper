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
package org.springframework.cloud.skipper.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.config.SkipperServerProperties;
import org.springframework.cloud.skipper.domain.PackageMetadata;
import org.springframework.cloud.skipper.index.PackageException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

/**
 * @author Mark Pollack
 */
@Service
public class PackageDownloadService implements ResourceLoaderAware {

	private final Logger logger = LoggerFactory.getLogger(PackageDownloadService.class);

	private ResourceLoader resourceLoader;

	private SkipperServerProperties skipperServerProperties;

	@Autowired
	public PackageDownloadService(SkipperServerProperties skipperServerProperties) {
		this.skipperServerProperties = skipperServerProperties;
	}

	public void downloadPackage(PackageMetadata packageMetadata) {
		String originUrl = packageMetadata.getOrigin();
		String sourceUrl = originUrl + "/" + packageMetadata.getName() + "/" +
				packageMetadata.getName() + "-" + packageMetadata.getVersion() + ".zip";
		Resource sourceResource = resourceLoader.getResource(sourceUrl);
		File targetPath = calculatePackageDirectory(packageMetadata);
		targetPath.mkdirs();
		File targetFile = calculatePackageZipFile(packageMetadata, targetPath);
		try {
			StreamUtils.copy(sourceResource.getInputStream(), new FileOutputStream(targetFile));
			logger.info("Downloaded package [" + packageMetadata.getName() + "-" + packageMetadata.getVersion()
					+ "] from " + originUrl);
		}
		catch (IOException e) {
			throw new PackageException("Could not copy " + sourceUrl + " to " + targetFile, e);
		}
		ZipUtil.unpack(targetFile, targetPath);
	}

	protected File calculatePackageZipFile(PackageMetadata packageMetadata, File targetPath) {
		return new File(targetPath, packageMetadata.getName() + "-" + packageMetadata.getVersion() + ".zip");
	}

	public File calculatePackageUnzippedDirectory(PackageMetadata packageMetadata, File targetPath) {
		return new File(targetPath, packageMetadata.getName() + "-" + packageMetadata.getVersion());
	}

	/**
	 * Give the PackageMetadata, return the directory where the package will be downloaded.
	 * The directory takes the server's PackageDir configuraiton property and appends the
	 * package name taken from the metadata.
	 * @param packageMetadata the package's metadata.
	 * @return The directory where the package will be downloaded.
	 */
	public File calculatePackageDirectory(PackageMetadata packageMetadata) {
		return new File(skipperServerProperties.getPackageDir()
				+ File.separator + packageMetadata.getName());
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
