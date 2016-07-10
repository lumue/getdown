package io.github.lumue.getdown.app.springboot.application;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.lumue.getdown.core.common.persistence.redis.RedisDownloadJobRepository;
import io.github.lumue.getdown.core.download.job.*;
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
			ContentLocationResolverRegistry contentLocationResolverRegistry,
			DownloadJobRepository downloadJobRepository,
			@Value("${getdown.path.download}") String downloadPath,
			EventBus eventbus) {
		ExecutorService executorService=Executors.newScheduledThreadPool(3);
		return new AsyncDownloadJobRunner(executorService,
				downloadJobRepository, contentLocationResolverRegistry, downloadPath, eventbus);
	}


	@Bean
	public DownloadService downloadService(
			DownloadJobRepository downloadJobRepository,
			AsyncDownloadJobRunner downloadJobRunner) {
		return new DownloadService(downloadJobRepository, downloadJobRunner);
	}

	@Bean
	public DownloadJobRepository downloadJobRepository(RedisTemplate<String,DownloadJob> downloadJobRedisTemplate)  throws IOException {
		return new RedisDownloadJobRepository(downloadJobRedisTemplate);
	}

	@Bean
	public RedisTemplate<String,DownloadJob> downloadJobRedisTemplate(JedisConnectionFactory jedisConnectionFactory){
		final RedisTemplate< String, DownloadJob > template = new RedisTemplate<>();
		template.setConnectionFactory( jedisConnectionFactory );
		template.setKeySerializer( new StringRedisSerializer());
		template.setHashValueSerializer(new GenericToStringSerializer<>(Long.class) );
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
