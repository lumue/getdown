package io.github.lumue.getdown.application;

import io.github.lumue.getdown.application.DownloadService.DownloadJobHandle;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

/**
 * in-memory queue for download jobs
 * 
 * uses ConcurrentLinkedQueue internally
 * 
 * @author lm
 *
 */
class VolatileDownloadJobQueue implements DownloadJobQueue {

	ConcurrentLinkedQueue<DownloadJob> jobQueue = new ConcurrentLinkedQueue<DownloadJob>();

	@Override
	public void queueJob(DownloadJob job) {
		jobQueue.add(job);
	}

	@Override
	public DownloadJob pollJob() {
		return jobQueue.poll();
	}

	@Override
	public Iterable<DownloadJob> listJobs() {
		return java.util.Collections.unmodifiableCollection(jobQueue);
	}

	@Override
	public Optional<DownloadJob> getJob(DownloadJobHandle jobHandle) {
		return jobQueue.stream().filter(new Predicate<DownloadJob>() {

			@Override
			public boolean test(DownloadJob t) {
				return jobHandle.equals(t.getHandle());
			}
		}).findFirst();
	}

	@Override
	public boolean hasNextJob() {
		return !jobQueue.isEmpty();
	}
}
