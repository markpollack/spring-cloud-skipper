package org.springframework.cloud.skipper.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.client.SkipperClient;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * @author Mark Pollack
 */
@Component
public class RollbackCommand implements CommandMarker {

	@Autowired
	private SkipperClient skipperClient;

	@CliCommand("skipper rollback")
	public String install(@CliOption(key = "releaseName", help = "Release name") String releaseName,
			@CliOption(key = "version", help = "Release version", mandatory = false, unspecifiedDefaultValue = "0") Integer releaseVersion) {

		Release release = skipperClient.rollback(releaseName, releaseVersion);

		StringBuilder sb = new StringBuilder();
		sb.append("Release Name: " + release.getName() + "\n");
		sb.append("Release Version: " + release.getVersion() + "\n");

		return sb.toString();
	}
}
