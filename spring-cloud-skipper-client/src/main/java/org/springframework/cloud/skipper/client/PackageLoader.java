package org.springframework.cloud.skipper.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.bind.YamlConfigurationFactory;
import org.springframework.cloud.skipper.domain.Metadata;
import org.springframework.cloud.skipper.domain.SkipperPackage;
import org.springframework.cloud.skipper.domain.Template;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;

/**
 * @author Mark Pollack
 */
public class PackageLoader {

	public String resolve(String packageName) {
		String name = StringUtils.trimAllWhitespace(packageName);
		File file = new File(name);
		if (file.exists()) {
			if (file.isDirectory()) {
				return name;
			}
		}
		throw new IllegalArgumentException("Can not locate package directory " + name);
	}

	public SkipperPackage load(String packagePath) {

		String resolvedPackagePath = resolve(packagePath);

		// Get all files under path
		List<File> files;
		try (Stream<Path> paths = Files.walk(Paths.get(resolvedPackagePath), 1)) {
			files = paths.map(i -> i.toAbsolutePath().toFile()).collect(Collectors.toList());
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Could not process files in path " + resolvedPackagePath, e);
		}

		SkipperPackage skipperPackage = new SkipperPackage();

		// Iterate over all files and "deserialize" the package.
		for (File file : files) {
			// Package metadata
			if (file.getName().equalsIgnoreCase("package.yaml") || file.getName().equalsIgnoreCase("package.yml")) {
				skipperPackage.setMetadata(loadPackageMetadata(file));
				continue;
			}
			// The template/manifiest file (but no templating supported ATM)
			String absFileName = file.getAbsoluteFile().toString();
			if (absFileName.endsWith("/templates")) {
				skipperPackage.setTemplates(loadTemplates(file));
				continue;
			}
			// Deal with recursive packages
			if (absFileName.endsWith("/packages")) {
				File[] directories = new File(file.getAbsolutePath()).listFiles(File::isDirectory);
				List<SkipperPackage> dependentPackages = new ArrayList<>();
				for (int i = 0; i < directories.length; i++) {
					dependentPackages.add(load(directories[i].getAbsolutePath()));
				}
				skipperPackage.setDependencies(dependentPackages.toArray(new SkipperPackage[dependentPackages.size()]));
				continue;
			}
		}

		return skipperPackage;
	}

	private Metadata loadPackageMetadata(File file) {
		YamlConfigurationFactory<Metadata> factory = new YamlConfigurationFactory<Metadata>(Metadata.class);
		factory.setResource(new FileSystemResource(file));
		try {
			return factory.getObject();
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Exception processing yaml file " + file.getName(), e);
		}

	}

	private Template[] loadTemplates(File templatePath) {
		List<File> files;
		try (Stream<Path> paths = Files.walk(Paths.get(templatePath.getAbsolutePath()), 1)) {
			files = paths.map(i -> i.toAbsolutePath().toFile()).collect(Collectors.toList());
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Could not process files in template path " + templatePath, e);
		}

		List<Template> templates = new ArrayList<>();
		for (File file : files) {
			if (isYamlFile(file)) {
				Template template = new Template();
				template.setName(file.getName());
				try {
					template.setData(new String(Files.readAllBytes(file.toPath()), "UTF-8"));
				}
				catch (IOException e) {
					throw new IllegalArgumentException("Could read template file " + file.getAbsoluteFile(), e);
				}
				templates.add(template);
			}
		}
		return templates.toArray(new Template[templates.size()]);
	}

	private boolean isYamlFile(File file) {
		Path path = Paths.get(file.getAbsolutePath());
		String fileName = path.getFileName().toString();
		if (!fileName.startsWith(".")) {
			return (fileName.endsWith("yml") || fileName.endsWith("yaml"));
		}
		return false;
	}

}
