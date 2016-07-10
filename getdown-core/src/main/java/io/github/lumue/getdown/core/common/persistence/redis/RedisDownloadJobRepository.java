package io.github.lumue.getdown.core.common.persistence.redis;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.util.StreamUtils;
import io.github.lumue.getdown.core.download.job.Download;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;

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

	public static final ScanOptions DEFAULT_SCAN_OPTIONS = ScanOptions.scanOptions().build();
	private final ZSetOperations<String, DownloadJob> redisOps;

	private final AtomicLong nextScoreValue;

	private final static String REDIS_COLLECTION_KEY=Download.class.getSimpleName();

	public RedisDownloadJobRepository(RedisTemplate<String, DownloadJob> redisTemplate) {
		this.redisOps = redisTemplate.opsForZSet();
		nextScoreValue=new AtomicLong(0);
		redisOps.scan(REDIS_COLLECTION_KEY, DEFAULT_SCAN_OPTIONS)
		.forEachRemaining(downloadJobTypedTuple -> {
			Double score = downloadJobTypedTuple.getScore();
			if(score >nextScoreValue.get()) {
				nextScoreValue.getAndSet(score.longValue());
			}
		});
	}



	@Override
	public DownloadJob create(ObjectBuilder<DownloadJob> builder) {
		Download.DownloadBuilder b= (Download.DownloadBuilder) builder;
		DownloadJob d = b.withIndex(nextScore()).build();
		this.redisOps.add(REDIS_COLLECTION_KEY,d,d.getIndex());
		return d;
	}

	private long nextScore() {
		return nextScoreValue.addAndGet(1);
	}

	@Override
	public Collection<DownloadJob> list() {
		Cursor<ZSetOperations.TypedTuple<DownloadJob>> cursor = this.redisOps.scan(REDIS_COLLECTION_KEY, DEFAULT_SCAN_OPTIONS);
		List<DownloadJob> jobList = StreamUtils.stream(cursor)
				.map((t) -> t.getValue())
				.collect(Collectors.toList());
		try {
			cursor.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return jobList;
	}

	@Override
	public Stream<DownloadJob> stream() {
		Cursor<ZSetOperations.TypedTuple<DownloadJob>> cursor = this.redisOps.scan(REDIS_COLLECTION_KEY, DEFAULT_SCAN_OPTIONS);
		return StreamUtils.stream(cursor)
				.map((t)->t.getValue());
	}

	@Override
	public void remove(Download.DownloadJobHandle handle) {
		this.redisOps.remove(REDIS_COLLECTION_KEY,get(handle));
	}

	@Override
	public DownloadJob get(Download.DownloadJobHandle handle) {
		Cursor<ZSetOperations.TypedTuple<DownloadJob>> cursor = this.redisOps.scan(REDIS_COLLECTION_KEY, DEFAULT_SCAN_OPTIONS);
		DownloadJob found = StreamUtils.stream(cursor)
				.map((t) -> t.getValue())
				.filter(job -> job.getHandle().equals(handle))
				.findFirst()
				.orElse(null);
		try {
			cursor.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return found;
	}

	@Override
	public void update(DownloadJob value) {
		return;
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public List<DownloadJob> findByJobState(Download.DownloadJobState state) {
		return null;
	}

	@Override
	public Stream<DownloadJob> streamByJobState(Download.DownloadJobState state) {
		return null;
	}
}
