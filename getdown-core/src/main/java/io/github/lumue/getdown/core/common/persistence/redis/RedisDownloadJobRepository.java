package io.github.lumue.getdown.core.common.persistence.redis;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.job.Download;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.stream.Stream;

/**
 * store downloadjob data in redis
 * Created by lm on 10.07.16.
 */
public class RedisDownloadJobRepository implements DownloadJobRepository{

	private final RedisTemplate<String,DownloadJob> redisTemplate;

	public RedisDownloadJobRepository(RedisTemplate<String, DownloadJob> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public DownloadJob create(ObjectBuilder<DownloadJob> builder) {
		return null;
	}

	@Override
	public List<DownloadJob> list() {
		return null;
	}

	@Override
	public Stream<DownloadJob> stream() {
		return null;
	}

	@Override
	public void remove(Download.DownloadJobHandle handle) {

	}

	@Override
	public DownloadJob get(Download.DownloadJobHandle handle) {
		return null;
	}

	@Override
	public void update(DownloadJob value) {

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
