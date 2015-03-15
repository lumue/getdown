package io.github.lumue.getdown.application;

import io.github.lumue.getdown.job.AsyncDownloadJobRunner;
import io.github.lumue.getdown.job.DownloadJobRepository;
import io.github.lumue.getdown.job.DownloadService;
import io.github.lumue.getdown.job.SingleJsonFileDownloadJobRepository;
import io.github.lumue.getdown.resolver.ContentLocationResolverRegistry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import reactor.core.Reactor;
import reactor.spring.context.config.EnableReactor;

@ComponentScan(basePackages = "io.github.lumue.getdown")
@EnableAutoConfiguration
@EnableReactor
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = "file://${getdown.path.config}/getdown.properties}")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

	@Bean
	public ExecutorService executorService() {
		return Executors.newCachedThreadPool();
	}
	
	@Bean
	public AsyncDownloadJobRunner downloadJobRunner(ExecutorService executorService,
			ContentLocationResolverRegistry contentLocationResolverRegistry, @Value("${getdown.path.download}") String downloadPath,
			Reactor reactor) {
		return new AsyncDownloadJobRunner(executorService, contentLocationResolverRegistry, downloadPath, reactor);
	}

	@Bean
	DownloadService downloadService(DownloadJobRepository downloadJobRepository, AsyncDownloadJobRunner downloadJobRunner) {
		return new DownloadService(downloadJobRepository, downloadJobRunner);
	}

	@Bean
	DownloadJobRepository downloadJobRepository(@Value("${getdown.path.repository}") String repositoryPath) {
		return new SingleJsonFileDownloadJobRepository(repositoryPath);
	}

	@Bean
	ContentLocationResolverRegistry contentLocationResolverRegistry() {
		return new ContentLocationResolverRegistry();
	}

}
