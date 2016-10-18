package io.github.lumue.getdown.core.common.persistence.redis;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.util.StreamUtils;
import io.github.lumue.getdown.core.download.job.Download;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.data.redis.support.collections.RedisZSet;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * store downloadjob data in redis
 * Created by lm on 10.07.16.
 */
public class RedisDownloadJobRepository implements DownloadJobRepository{

	private final static String REDIS_COLLECTION_KEY=Download.class.getSimpleName();
	private static final Logger LOGGER= LoggerFactory.getLogger(RedisDownloadJobRepository.class);

	private final RedisZSet<DownloadJob> redisZSet;
	private final AtomicLong nextScoreValue;


	public RedisDownloadJobRepository(RedisZSet<DownloadJob> zSet) {
		this.redisZSet=zSet;
		nextScoreValue=new AtomicLong(0);


	}

	@PostConstruct
	public void initScore() {
		RedisZSet<DownloadJob> zSet=redisZSet;
		try {
			zSet.scan()
			.forEachRemaining(downloadJob -> {
				Long score = downloadJob.getIndex();
				if(score >nextScoreValue.get()) {
					nextScoreValue.getAndSet(score);
				}
			});
		} catch (Exception e) {
			LOGGER.warn("could not scan for next highest score value",e);
		}
	}

	public RedisDownloadJobRepository(RedisTemplate<String, DownloadJob> downloadJobRedisTemplate) {
		this(new DefaultRedisZSet<>(downloadJobRedisTemplate.boundZSetOps(REDIS_COLLECTION_KEY)));
	}


	@Override
	public DownloadJob create(ObjectBuilder<DownloadJob> builder) {
		Download.DownloadBuilder b= (Download.DownloadBuilder) builder;
		DownloadJob d = b.withIndex(nextScore()).build();
		this.redisZSet.add(d,d.getIndex());
		return d;
	}

	private long nextScore() {
		return nextScoreValue.addAndGet(1);
	}

	@Override
	public Collection<DownloadJob> list() {
		return redisZSet;
	}

	@Override
	public Stream<DownloadJob> stream() {
		return redisZSet.stream();
	}

	@Override
	public void remove(Download.DownloadJobHandle handle) {
		DownloadJob job = get(handle);
		this.redisZSet.removeByScore(job.getIndex(),job.getIndex());
	}

	@Override
	public DownloadJob get(Download.DownloadJobHandle handle) {
		return this.stream()
				.filter(job -> job.getHandle().equals(handle))
				.findFirst()
				.orElse(null);
	}

	@Override
	public void update(DownloadJob value) {
		this.redisZSet.removeByScore(value.getIndex(),value.getIndex());
		this.redisZSet.add(value,value.getIndex());
	}

	@Override
	public void close() throws Exception {
	}

}
