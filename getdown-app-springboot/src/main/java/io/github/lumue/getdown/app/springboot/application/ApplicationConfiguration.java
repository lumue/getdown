package io.github.lumue.getdown.app.springboot.application;

import io.github.lumue.getdown.core.common.persistence.jdkserializable.JdkSerializableDownloadJobRepository;
import io.github.lumue.getdown.core.download.job.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import reactor.bus.EventBus;
import reactor.core.publisher.TopicProcessor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
			@Value("${getdown.jobrunner.threads.download}") Integer threadsDownload) {

		return new AsyncDownloadJobRunner(
				threadsPrepare,
				threadsDownload);
	}




	@Bean
	public DownloadService downloadService(
			DownloadJobRepository downloadJobRepository,
			AsyncDownloadJobRunner downloadJobRunner,
			@Value("${getdown.path.download}") String downloadPath,
			EventBus eventbus,
			UrlProcessor urlProcessor) {
		return new DownloadService(downloadJobRepository, downloadJobRunner, downloadPath, eventbus, urlProcessor);
	}

	@Bean
	public UrlProcessor urlProcessor(){
		return ChainingUrlProcessor.newBuilder()
				.add(in -> {
					if(in.startsWith("http://vivo.sx"))
						return in.replace("http://vivo.sx","https://vivo.sx");
					return in;
				})
				.build();


	}



	@Bean
	public DownloadJobRepository downloadJobRepository(@Value("getdown.path.repository") String path)  throws IOException {
		return new JdkSerializableDownloadJobRepository(path);
	}



	@Bean public TopicProcessor dispatcher(){
		return TopicProcessor.create();
	}
	
	@Bean
	public EventBus eventBus(TopicProcessor dispatcher) {
		return EventBus.create(dispatcher);
	}


}
