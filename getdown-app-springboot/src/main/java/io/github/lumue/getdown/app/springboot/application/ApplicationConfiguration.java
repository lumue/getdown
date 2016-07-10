package io.github.lumue.getdown.app.springboot.application;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.github.lumue.getdown.core.common.persistence.jdkserializable.JdkSerializableDownloadJobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.github.lumue.getdown.core.download.job.AsyncDownloadJobRunner;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;
import io.github.lumue.getdown.core.download.job.DownloadService;
import io.github.lumue.getdown.core.download.job.ContentLocationResolverRegistry;
import reactor.bus.EventBus;
import reactor.core.publisher.TopicProcessor;

@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "file://${getdown.path.config}/getdown.properties}")
public class ApplicationConfiguration {
	@Bean
	public ExecutorService executorService() {
		return Executors.newCachedThreadPool();
	}

	@Bean
	public AsyncDownloadJobRunner downloadJobRunner(
			@Value("${getdown.jobrunner.threads.prepare}") Integer threadsPrepare,
			@Value("${getdown.jobrunner.threads.download}") Integer threadsDownload,
			EventBus eventbus) {

		return new AsyncDownloadJobRunner(
				threadsPrepare,
				threadsDownload);
	}




	@Bean
	public DownloadService downloadService(
			DownloadJobRepository downloadJobRepository,
			AsyncDownloadJobRunner downloadJobRunner,
			@Value("${getdown.path.download}") String downloadPath,
			EventBus eventbus) {
		return new DownloadService(downloadJobRepository, downloadJobRunner, downloadPath, eventbus);
	}

	@Bean
	public DownloadJobRepository downloadJobRepository(
			@Value("${getdown.path.repository}") String repositoryPath) throws IOException {
		return new JdkSerializableDownloadJobRepository(repositoryPath);
	}

	@Bean
	public ContentLocationResolverRegistry contentLocationResolverRegistry() {
		return new ContentLocationResolverRegistry();
	}

	@Bean public TopicProcessor dispatcher(){
		return TopicProcessor.create();
	}
	
	@Bean
	public EventBus eventBus(TopicProcessor dispatcher) {
		return EventBus.create(dispatcher);
	}
}
