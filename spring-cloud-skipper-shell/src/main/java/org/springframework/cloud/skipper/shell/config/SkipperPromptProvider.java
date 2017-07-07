package org.springframework.cloud.skipper.shell.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SkipperPromptProvider implements PromptProvider {

	private String operations;

	@Override
	public String getProviderName() {
		return "skipper";
	}

	@Override
	public String getPrompt() {
		// if (shell.getDataFlowOperations() == null) {
		if (operations != null) {
			return "server-unknown:>";
		}
		else {
			return "skipper:>";
		}
	}
}
