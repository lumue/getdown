package io.github.lumue.getdown.application;


import io.github.lumue.getdown.application.DownloadJob.DownloadJobHandle;

import java.util.Optional;


interface DownloadJobQueue {

	void queueJob(DownloadJob job);

	DownloadJob pollJob();

	public Iterable<DownloadJob> listJobs();

	public Optional<DownloadJob> getJob(DownloadJobHandle jobHandle);

	boolean hasNextJob();
}
