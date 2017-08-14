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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.domain.InstallProperties;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.index.PackageMetadata;
import org.springframework.cloud.skipper.repository.PackageMetadataRepository;
import org.springframework.stereotype.Service;

/**
 * @author Mark Pollack
 */
@Service
public class ReleaseService {

	private PackageMetadataRepository packageMetadataRepository;

	private PackageDownloadService packageDownloadService;

	@Autowired
	public ReleaseService(PackageMetadataRepository packageMetadataRepository,
						  PackageDownloadService packageDownloadService) {
		this.packageMetadataRepository = packageMetadataRepository;
		this.packageDownloadService = packageDownloadService;
	}

	public Release install(long id, InstallProperties installProperties) {
		PackageMetadata packageMetadata = packageMetadataRepository.findOne(id);
		packageDownloadService.downloadPackage(packageMetadata);

		return null;
	}
}
