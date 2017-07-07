package org.springframework.cloud.skipper.shell.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.client.SkipperClient;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

@Component
public class VersionCommand implements CommandMarker {

	@Autowired
	private SkipperClient skipperClient;

	@CliCommand("skipper version")
	public String version() {
		return skipperClient.version();
	}
}
