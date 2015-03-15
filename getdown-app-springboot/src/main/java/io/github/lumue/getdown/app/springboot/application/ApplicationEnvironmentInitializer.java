package io.github.lumue.getdown.app.springboot.application;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

/**
 * set up application environment create paths, default.properties etc.
 * 
 * @author lm
 *
 */
@Component
public class ApplicationEnvironmentInitializer implements InitializingBean{

	@Value("${getdown.path.home}")
	private FileSystemResource homePath;
	@Value("${getdown.path.config}")
	private FileSystemResource configPath;
	@Value("${getdown.path.log}")
	private FileSystemResource logPath;
	@Value("${getdown.path.download}")
	private FileSystemResource downloadPath;
	@Value("${getdown.path.repository}")
	private FileSystemResource repositoryPath;

	@Override
	public void afterPropertiesSet() throws Exception {
		initEnvironment();
	}

	public void initEnvironment() {
		createIfNotExists(homePath);
		createIfNotExists(configPath);
		createIfNotExists(logPath);
		createIfNotExists(downloadPath);
		createIfNotExists(repositoryPath);
	}

	private void createIfNotExists(FileSystemResource fileSystemResource) {
		if (!fileSystemResource.exists())
			fileSystemResource.getFile().mkdir();
	}
}
