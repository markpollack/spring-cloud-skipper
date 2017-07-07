package org.springframework.cloud.skipper.server.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.springframework.cloud.skipper.domain.Release;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

// TODO - a git based impl
@Component
public class FileSystemManifestRepository implements ManifestRepository {

	private final String homeDir = System.getProperty("user.home");

	@Override
	public void save(Release release) {
		final File releaseDir = new File(homeDir + File.separator + "manifests" + File.separator
				+ release.getName() + "-v"
				+ release.getVersion());
		releaseDir.mkdirs();
		File manifestFile = new File(releaseDir, "manifest.yml");
		writeText(manifestFile, release.getManifest());

	}

	private void writeText(final File target, final String body) {
		try (OutputStream stream = new FileOutputStream(target, false)) {
			StreamUtils.copy(body, Charset.forName("UTF-8"), stream);
		}
		catch (final Exception e) {
			throw new IllegalStateException("Cannot write file " + target, e);
		}
	}
}
