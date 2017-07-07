package org.springframework.cloud.skipper.shell;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.dataflow.shell.autoconfigure.BaseShellAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { BaseShellAutoConfiguration.class })
public class SkipperShellApplication {

	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder().sources(SkipperShellApplication.class).bannerMode(Banner.Mode.OFF).run(args);
	}
}
