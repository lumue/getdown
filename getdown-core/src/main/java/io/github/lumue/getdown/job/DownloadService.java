package io.github.lumue.getdown.job;

import io.github.lumue.getdown.job.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.job.HttpDownloadJob.HttpDownloadJobBuilder;
import io.github.lumue.getdown.persistence.ObjectBuilder;

import java.net.URI;

/**
 * manage the execution of downloads
 * 
 * @author lm
 *
 */
public class DownloadService {


	private final DownloadJobRepository jobRepository;

	private final AsyncDownloadJobRunner downloadJobRunner;

	
	public DownloadService(DownloadJobRepository jobRepository, AsyncDownloadJobRunner downloadJobRunner) {
		super();
		this.jobRepository = jobRepository;
		this.downloadJobRunner = downloadJobRunner;
	}

	public DownloadJob addDownload(final String url) {
		String filename = resolveFilename(url);
		ObjectBuilder<DownloadJob> jobBuilder = new HttpDownloadJobBuilder().withUrl(url).withOutputFilename(filename);
		return jobRepository.create(jobBuilder);
	}

	public void startDownload(final DownloadJobHandle handle) {
		DownloadJob job = jobRepository.get(handle);
		downloadJobRunner.runJob(job);
	}
	
	public Iterable<DownloadJob> listDownloads(){
		return this.jobRepository.list();
	}

	private String resolveFilename(final String url) {
		String path = URI.create(url).getPath();
		return path.substring(path.lastIndexOf('/') + 1).toString();
	}

}
