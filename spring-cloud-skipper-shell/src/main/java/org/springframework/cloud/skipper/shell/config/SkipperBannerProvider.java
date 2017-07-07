package org.springframework.cloud.skipper.shell.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.shell.support.util.FileUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SkipperBannerProvider implements BannerProvider {

	private static final String WELCOME = "Welcome to the Spring Cloud Skipper shell. For assistance hit TAB or "
			+ "type \"help\".";

	@Override
	public String getProviderName() {
		return "skipper";
	}

	@Override
	public String getBanner() {
		return FileUtils.readBanner(SkipperBannerProvider.class, "/skipper-banner.txt") + "\n" + getVersion() + "\n";
	}

	/**
	 * Returns the version information as found in the manifest file (set during release).
	 */
	@Override
	public String getVersion() {
		Package pkg = SkipperBannerProvider.class.getPackage();
		String version = null;
		if (pkg != null) {
			version = pkg.getImplementationVersion();
		}
		return (version != null ? version : "Unknown Version");
	}

	@Override
	public String getWelcomeMessage() {
		return WELCOME;
	}

}
