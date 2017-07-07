package org.springframework.cloud.skipper.domain;

/**
 * @author Mark Pollack
 */
public class SkipperPackage {

	private Metadata metadata;

	private Template[] templates;

	private SkipperPackage[] dependencies;

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public SkipperPackage[] getDependencies() {
		return dependencies;
	}

	public void setDependencies(SkipperPackage[] dependencies) {
		this.dependencies = dependencies;
	}

	public Template[] getTemplates() {
		return templates;
	}

	public void setTemplates(Template[] templates) {
		this.templates = templates;
	}
}
