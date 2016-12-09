package io.github.lumue.getdown.webapp;

import io.github.lumue.getdown.core.common.persistence.jdkserializable.JdkSerializableDownloadTaskRepository;
import io.github.lumue.getdown.core.download.DownloadService;
import io.github.lumue.getdown.core.download.files.WorkPathManager;
import io.github.lumue.getdown.core.download.job.*;
import io.github.lumue.getdown.core.download.task.DownloadTaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import reactor.bus.EventBus;
import reactor.core.publisher.TopicProcessor;

import java.io.IOException;
import java.nio.file.Paths;
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
			@Value("${getdown.jobrunner.threads.download}") Integer threadsDownload,
			@Value("${getdown.jobrunner.threads.postprocess}") Integer threadsPostprocess) {

		return new AsyncDownloadJobRunner(
				threadsPrepare,
				threadsDownload,
				threadsPrepare);
	}




	@Bean
	public DownloadService downloadService(
			DownloadTaskRepository downloadTaskRepository,
			AsyncDownloadJobRunner downloadJobRunner,
			@Value("${getdown.path.media}") String downloadPath,
			EventBus eventbus,
			UrlProcessor urlProcessor,
			WorkPathManager workPathManager) {
		return new DownloadService(downloadTaskRepository, downloadJobRunner, downloadPath, eventbus, urlProcessor, workPathManager);
	}

	@Bean
	public WorkPathManager workPathManager(@Value("${getdown.path.download}") String workPath) throws IOException {
		return new WorkPathManager(Paths.get(workPath));
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
	public DownloadTaskRepository downloadTaskRepository(@Value("${getdown.path.repository}") String path)  throws IOException {
		return new JdkSerializableDownloadTaskRepository(path);
	}



	@Bean public TopicProcessor dispatcher(){
		return TopicProcessor.create();
	}
	
	@Bean
	public EventBus eventBus(TopicProcessor dispatcher) {
		return EventBus.create(dispatcher);
	}


}
