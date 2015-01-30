package io.github.lumue.getdown.application;

import io.github.lumue.getdown.job.AsyncDownloadJobRunner;
import io.github.lumue.getdown.job.DownloadJobRepository;
import io.github.lumue.getdown.job.DownloadJobRunner;
import io.github.lumue.getdown.job.DownloadService;
import io.github.lumue.getdown.jobrepo.hazel.HazelJobRepository;
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

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

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
	HazelcastInstance hazelcastInstance() {
		return Hazelcast.newHazelcastInstance();
	}

	@Bean
	public HazelJobRepository hazelcastDownloadJobRepository(HazelcastInstance hazelcastInstance) {
		return new HazelJobRepository(hazelcastInstance);
	}

	@Bean
	DownloadService downloadService(DownloadJobRepository downloadJobRepository, DownloadJobRunner downloadJobRunner) {
		return new DownloadService(downloadJobRepository, downloadJobRunner);
	}

	@Bean
	ContentLocationResolverRegistry contentLocationResolverRegistry() {
		return new ContentLocationResolverRegistry();
	}

}
