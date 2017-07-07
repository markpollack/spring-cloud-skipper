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
public class UpdateCommand implements CommandMarker {

	@Autowired
	private SkipperClient skipperClient;

	@CliCommand("skipper update")
	public String install(
			@CliOption(mandatory = true, key = { "", "packagePath" }, help = "Package path") String packagePath,
			@CliOption(key = "releaseName", help = "Release name") String releaseName) {

		Release release = skipperClient.update(packagePath, releaseName);

		StringBuilder sb = new StringBuilder();
		sb.append("Release Name: " + release.getName() + "\n");
		sb.append("Release Version: " + release.getVersion() + "\n");

		return sb.toString();
	}
}
