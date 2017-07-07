package org.springframework.cloud.skipper.shell.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.dataflow.shell.ShellCommandLineParser;
import org.springframework.cloud.dataflow.shell.ShellCommandLineRunner;
import org.springframework.cloud.dataflow.shell.ShellProperties;
import org.springframework.cloud.dataflow.shell.TargetHolder;
import org.springframework.cloud.skipper.client.SkipperClientConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.shell.CommandLine;
import org.springframework.shell.core.JLineShell;
import org.springframework.shell.core.JLineShellComponent;

/**
 * Configures the various commands that are part of the default Spring Shell experience.
 *
 * @author Josh Long
 * @author Mark Pollack
 * @author Eric Bottard
 */
@Configuration
@ImportResource("classpath*:/META-INF/spring/spring-shell-plugin.xml")
@Import(SkipperClientConfiguration.class)
public class SkipperShellConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(SkipperShellConfiguration.class);

	@Autowired
	private CommandLine commandLine;

	@Bean
	public TargetHolder targetHolder() {
		return new TargetHolder();
	}

	@Bean
	public ShellCommandLineParser shellCommandLineParser() {
		return new ShellCommandLineParser();
	}

	@Bean
	public ShellProperties shellProperties() {
		return new ShellProperties();
	}

	/**
	 * Return the interactive command line runner. Note: Add a
	 * {@link ConditionalOnMissingBean} annotation is used so that this interactive
	 * command line runner is not created when running the shell in the same process as
	 * the Gilligan server.
	 *
	 * @return the interactive shell
	 */
	@Bean
	public ShellCommandLineRunner commandLineRunner() {
		return new ShellCommandLineRunner();
	}

	@Bean
	@ConditionalOnMissingBean(CommandLine.class)
	public CommandLine commandLine(ShellCommandLineParser shellCommandLineParser, ShellProperties shellProperties,
			ApplicationArguments applicationArguments) throws Exception {
		return shellCommandLineParser.parse(shellProperties, applicationArguments.getSourceArgs());
	}

	@Bean
	@ConditionalOnMissingBean(JLineShell.class)
	public JLineShellComponent shell() {
		return new JLineShellComponent();
	}

	@Configuration
	@ComponentScan({ "org.springframework.shell.converters", "org.springframework.shell.plugin.support" })
	public static class DefaultShellComponents {

		@PostConstruct
		public void log() {
			logger.debug(
					"default (o.s.shell.{converters,plugin.support})" + " Spring Shell packages are being scanned");
		}
	}

	@Configuration
	@ComponentScan({ "org.springframework.shell.commands", "org.springframework.cloud.skipper.shell.commands",
			"org.springframework.cloud.skipper.shell.config" })
	public static class RegisterInternalCommands {

		@PostConstruct
		public void log() {
			logger.debug("(o.s.shell.commands) Spring Shell" + " packages are being scanned");
			logger.debug("(o.s.c.skipper.shell.command) Spring Cloud Skipper Shell" + " packages are being scanned");
		}
	}
}
