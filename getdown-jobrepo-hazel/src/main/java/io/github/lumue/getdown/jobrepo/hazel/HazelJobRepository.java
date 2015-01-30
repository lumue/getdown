package io.github.lumue.getdown.jobrepo.hazel;

import io.github.lumue.getdown.downloader.ContentDownloader.DownloadState;
import io.github.lumue.getdown.job.DownloadJob;
import io.github.lumue.getdown.job.DownloadJob.DownloadJobBuilder;
import io.github.lumue.getdown.job.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.job.DownloadJobRepository;
import io.github.lumue.getdown.job.MapBasedDownloadJobRepository;

import com.hazelcast.core.HazelcastInstance;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class HazelJobRepository implements DownloadJobRepository {

	private static final String HAZELCAST_MAP_NAME = "io.github.lumue.getdown.jobrepo.hazel.HazelJobRepository.jobMap";

	private final MapBasedDownloadJobRepository mapBasedJobRepository;

	public HazelJobRepository(HazelcastInstance hazelcastInstance) {
		super();
		mapBasedJobRepository = new MapBasedDownloadJobRepository(hazelcastInstance.getMap(HAZELCAST_MAP_NAME));
	}

	@Override
	public DownloadJob create(DownloadJobBuilder downloadJobBuilder) {
		return mapBasedJobRepository.create(downloadJobBuilder);
	}

	@Override
	public Iterable<DownloadJob> list() {
		return mapBasedJobRepository.list();
	}

	@Override
	public Iterable<DownloadJob> findByDownloadState(DownloadState downloadState) {
		return mapBasedJobRepository.findByDownloadState(downloadState);
	}

	@Override
	public void remove(DownloadJobHandle handle) {
		mapBasedJobRepository.remove(handle);
	}

	@Override
	public DownloadJob get(DownloadJobHandle handle) {
		return mapBasedJobRepository.get(handle);
	}



}
