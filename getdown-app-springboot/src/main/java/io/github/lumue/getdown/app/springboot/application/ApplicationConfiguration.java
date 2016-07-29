package io.github.lumue.getdown.app.springboot.application;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.lumue.getdown.core.common.persistence.redis.RedisDownloadJobRepository;
import io.github.lumue.getdown.core.download.job.*;
import io.github.lumue.getdown.core.common.persistence.jdkserializable.JdkSerializableDownloadJobRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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
			EventBus eventbus) {
		return new DownloadService(downloadJobRepository, downloadJobRunner, downloadPath, eventbus);
	}





	@Bean
	public DownloadJobRepository downloadJobRepository(RedisTemplate<String,DownloadJob> downloadJobRedisTemplate)  throws IOException {
		return new RedisDownloadJobRepository(downloadJobRedisTemplate);
	}

	@Bean
	public RedisTemplate<String,DownloadJob> downloadJobRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
		final RedisTemplate<String, DownloadJob> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class));
		template.setValueSerializer(new Jackson2JsonRedisSerializer<>(DownloadJob.class));
		return template;
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
