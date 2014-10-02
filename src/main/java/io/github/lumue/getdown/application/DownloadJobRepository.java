package io.github.lumue.getdown.application;

import io.github.lumue.getdown.application.DownloadJob.DownloadJobBuilder;
import io.github.lumue.getdown.application.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.downloader.ContentDownloader.DownloadState;

interface DownloadJobRepository {

	public DownloadJob create(DownloadJobBuilder builder);

	public Iterable<DownloadJob> list();

	public Iterable<DownloadJob> findByDownloadState(DownloadState downloadState);

	public void remove(DownloadJobHandle handle);

	public DownloadJob get(DownloadJobHandle handle);

}
