package io.github.lumue.getdown.job;

import io.github.lumue.getdown.downloader.ContentDownloader.DownloadState;
import io.github.lumue.getdown.job.DownloadJob.DownloadJobBuilder;
import io.github.lumue.getdown.job.DownloadJob.DownloadJobHandle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class MapBasedDownloadJobRepository implements DownloadJobRepository {

	private final Map<DownloadJobHandle, DownloadJob> jobMap;

	public MapBasedDownloadJobRepository(Map<DownloadJobHandle, DownloadJob> jobMap) {
		super();
		this.jobMap = jobMap;
	}

	MapBasedDownloadJobRepository() {
		this(new HashMap<DownloadJobHandle, DownloadJob>());
	}

	@Override
	public DownloadJob create(DownloadJobBuilder downloadJobBuilder) {
		DownloadJob job = downloadJobBuilder.build();
		jobMap.put(job.getHandle(), job);
		return job;
	}

	@Override
	public Iterable<DownloadJob> list() {
		return java.util.Collections.unmodifiableCollection(jobMap.values());
	}

	@Override
	public Iterable<DownloadJob> findByDownloadState(DownloadState downloadState) {
		return new Iterable<DownloadJob>() {

			@Override
			public Iterator<DownloadJob> iterator() {
				return jobMap.values().stream().filter(new Predicate<DownloadJob>() {

					@Override
					public boolean test(DownloadJob t) {
						return downloadState.equals(t.getProgressListener().getState());
					}

				}).iterator();
			}
		};

	}

	@Override
	public void remove(DownloadJobHandle handle) {
		jobMap.remove(handle);
	}

	@Override
	public DownloadJob get(DownloadJobHandle handle) {
		return jobMap.get(handle);
	}



}
