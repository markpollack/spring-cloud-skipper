/*
 * Copyright 2017-2018 the original author or authors.
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
package org.springframework.cloud.skipper.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for the packages.
 *
 * @author Mark Pollack
 * @author Gunnar Hillert
 *
 */
@Entity
@Table(name = "SkipperRepository",
	uniqueConstraints = @UniqueConstraint(name = "uk_repository", columnNames = { "name" }))
public class Repository extends AbstractEntity {

	/**
	 * A short name, e.g. 'stable' to associate with this repository, must be unique.
	 */
	@NotNull
	private String name;

	/**
	 * The root url that points to the location of an index.yaml file and other files
	 * supporting the index e.g. myapp-1.0.0.zip, icons-64x64.zip
	 */
	@NotNull
	private String url;

	/**
	 * The url that points to the source package files that was used to create the index and
	 * packages.
	 */
	private String sourceUrl;

	/**
	 * Is this a local or remote repository. Uploads are only allowed to a local repository
	 */
	private Boolean local = false;

	/**
	 * A short description of the repository.
	 */
	private String description;

	/**
	 * An integer used to determine which repository is preferred over others when searching
	 * for a package.
	 */
	private Integer repoOrder;

	@OneToMany(
			mappedBy = "repository",
			cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH}
	)
	@JsonBackReference
	private List<PackageMetadata> packageMetadataList = new ArrayList<>();

	// TODO security/checksum fields of referenced index file.

	public Repository() {
	}

	public void addPackageMetadata(PackageMetadata packageMetadata) {
		packageMetadataList.add(packageMetadata);
		packageMetadata.setRepository(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getRepoOrder() {
		return repoOrder;
	}

	public void setRepoOrder(Integer repoOrder) {
		this.repoOrder = repoOrder;
	}

	public List<PackageMetadata> getPackageMetadataList() {
		return packageMetadataList;
	}

	public void setPackageMetadataList(List<PackageMetadata> packageMetadataList) {
		this.packageMetadataList = packageMetadataList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || !(o instanceof Repository)) return false;

		Repository that = (Repository) o;

		if (!name.equals(that.name)) return false;
		if (url != null ? !url.equals(that.url) : that.url != null) return false;
		if (sourceUrl != null ? !sourceUrl.equals(that.sourceUrl) : that.sourceUrl != null) return false;
		if (local != null ? !local.equals(that.local) : that.local != null) return false;
		if (description != null ? !description.equals(that.description) : that.description != null) return false;
		return repoOrder != null ? repoOrder.equals(that.repoOrder) : that.repoOrder == null;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (url != null ? url.hashCode() : 0);
		result = 31 * result + (sourceUrl != null ? sourceUrl.hashCode() : 0);
		result = 31 * result + (local != null ? local.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		result = 31 * result + (repoOrder != null ? repoOrder.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Repository{" +
				"name='" + name + '\'' +
				", url='" + url + '\'' +
				", local=" + local +
				'}';
	}
}
