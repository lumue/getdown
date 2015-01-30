package io.github.lumue.getdown.job;

import io.github.lumue.getdown.downloader.ContentDownloader.DownloadState;
import io.github.lumue.getdown.job.DownloadJob.DownloadJobBuilder;
import io.github.lumue.getdown.job.DownloadJob.DownloadJobHandle;

public interface DownloadJobRepository {

	public DownloadJob create(DownloadJobBuilder builder);

	public Iterable<DownloadJob> list();

	public Iterable<DownloadJob> findByDownloadState(DownloadState downloadState);

	public void remove(DownloadJobHandle handle);

	public DownloadJob get(DownloadJobHandle handle);

}
