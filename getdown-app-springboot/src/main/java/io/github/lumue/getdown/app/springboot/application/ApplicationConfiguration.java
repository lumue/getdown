package io.github.lumue.getdown.app.springboot.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.github.lumue.getdown.core.download.job.AsyncDownloadJobRunner;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;
import io.github.lumue.getdown.core.download.job.DownloadService;
import io.github.lumue.getdown.core.download.job.FilePersistentDownloadJobRepository;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolverRegistry;
import reactor.bus.EventBus;
import reactor.core.Dispatcher;
import reactor.core.dispatch.ThreadPoolExecutorDispatcher;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "file://${getdown.path.config}/getdown.properties}")
public class ApplicationConfiguration {
	@Bean
	public ExecutorService executorService() {
		return Executors.newCachedThreadPool();
	}

	@Bean
	public AsyncDownloadJobRunner downloadJobRunner(
			ExecutorService executorService,
			ContentLocationResolverRegistry contentLocationResolverRegistry,
			@Value("${getdown.path.download}") String downloadPath,
			EventBus eventbus) {
		return new AsyncDownloadJobRunner(executorService,
				contentLocationResolverRegistry, downloadPath, eventbus);
	}


	@Bean
	public DownloadService downloadService(
			DownloadJobRepository downloadJobRepository,
			AsyncDownloadJobRunner downloadJobRunner) {
		return new DownloadService(downloadJobRepository, downloadJobRunner);
	}

	@Bean
	public DownloadJobRepository downloadJobRepository(
			@Value("${getdown.path.repository}") String repositoryPath) {
		return new FilePersistentDownloadJobRepository(repositoryPath);
	}

	@Bean
	public ContentLocationResolverRegistry contentLocationResolverRegistry() {
		return new ContentLocationResolverRegistry();
	}

	@Bean public Dispatcher dispatcher(){
		return new ThreadPoolExecutorDispatcher(20, 1000);
	}
	
	@Bean
	public EventBus eventBus(Dispatcher dispatcher) {
		return EventBus.create(dispatcher);
	}
}
