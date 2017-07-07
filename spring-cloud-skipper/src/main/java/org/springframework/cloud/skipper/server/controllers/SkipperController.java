package org.springframework.cloud.skipper.server.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SkipperController {

	@GetMapping
	@RequestMapping("/version")
	public String version() {
		return "1.0.0";
	}
}
