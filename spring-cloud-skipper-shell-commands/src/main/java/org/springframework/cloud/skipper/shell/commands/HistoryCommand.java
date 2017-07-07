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
public class HistoryCommand implements CommandMarker {

	@Autowired
	private SkipperClient skipperClient;

	@CliCommand("skipper history")
	public String install(
			@CliOption(key = { "", "releaseName" }, help = "Release name", mandatory = true) String releaseName) {

		Release[] releases = skipperClient.history(releaseName);

		StringBuilder sb = new StringBuilder();
		for (Release release : releases) {
			sb.append("-----\n");
			sb.append("Release Name: " + release.getName() + "\n");
			sb.append("Release Version: " + release.getVersion() + "\n");
		}
		sb.append("-----\n");
		return sb.toString();
	}
}
