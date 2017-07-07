package org.springframework.cloud.skipper.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.skipper.api.*;
import org.springframework.cloud.skipper.domain.Release;
import org.springframework.cloud.skipper.server.service.ReleaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SkipperController {

	private ReleaseService releaseService;

	@Autowired
	public SkipperController(ReleaseService releaseService) {
		this.releaseService = releaseService;
	}

	@GetMapping
	@RequestMapping("/version")
	public String version() {
		return "1.0.0";
	}

	@PostMapping
	@RequestMapping("/install")
	@ResponseStatus(HttpStatus.CREATED)
	public Release install(@RequestBody InstallReleaseRequest installReleaseRequest) {
		Release release = createInitial(installReleaseRequest);
		releaseService.install(release, installReleaseRequest.getSkipperPackage());
		return release;
	}

	@PostMapping
	@RequestMapping("/update")
	@ResponseStatus(HttpStatus.CREATED)
	public Release update(@RequestBody UpdateReleaseRequest updateReleaseRequest) {
		return releaseService.update(updateReleaseRequest.getName(),
				updateReleaseRequest.getSkipperPackage());
	}

	@PostMapping
	@RequestMapping("/rollback")
	@ResponseStatus(HttpStatus.CREATED)
	public Release rollback(@RequestBody RollbackReleaseRequest rollbackReleaseRequest) {
		return releaseService.rollback(rollbackReleaseRequest.getName(), rollbackReleaseRequest.getVersion());
	}

	@PostMapping
	@RequestMapping("/history")
	@ResponseStatus(HttpStatus.CREATED)
	public HistoryResponse history(@RequestBody HistoryRequest historyRequest) {
		Release[] releases = releaseService.history(historyRequest.getName());
		HistoryResponse response = new HistoryResponse();
		response.setReleases(releases);
		return response;
	}

	private Release createInitial(InstallReleaseRequest installReleaseRequest) {
		Release release = new Release();
		release.setName(installReleaseRequest.getName());
		release.setSkipperPackage(installReleaseRequest.getSkipperPackage());
		release.setVersion(1);
		return release;
	}
}
