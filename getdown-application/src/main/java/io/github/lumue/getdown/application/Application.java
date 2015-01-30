package io.github.lumue.getdown.application;

import io.github.lumue.getdown.job.AsyncDownloadJobRunner;
import io.github.lumue.getdown.job.DownloadJobRunner;
import io.github.lumue.getdown.job.DownloadService;
import io.github.lumue.getdown.job.VolatileDownloadJobRepository;
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

@ComponentScan(basePackages = "io.github.lumue.getdown")
@EnableAutoConfiguration
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
	public DownloadJobRunner downloadJobRunner(ExecutorService executorService,
			ContentLocationResolverRegistry contentLocationResolverRegistry, @Value("${getdown.path.download}") String downloadPath) {
		return new AsyncDownloadJobRunner(executorService, contentLocationResolverRegistry, downloadPath);
	}

	@Bean
	DownloadService downloadService(DownloadJobRunner downloadJobRunner) {
		return new DownloadService(new VolatileDownloadJobRepository(), downloadJobRunner);
	}

	@Bean
	ContentLocationResolverRegistry contentLocationResolverRegistry() {
		return new ContentLocationResolverRegistry();
	}

}
