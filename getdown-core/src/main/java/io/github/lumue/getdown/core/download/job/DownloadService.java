package io.github.lumue.getdown.core.download.job;

import java.net.URI;
import java.util.stream.Stream;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.downloader.youtubedl.YoutubedlDownloadJob;
import io.github.lumue.getdown.core.download.job.Download.DownloadJobHandle;

import static io.github.lumue.getdown.core.download.job.Download.DownloadJobState.*;

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
		ObjectBuilder<DownloadJob> jobBuilder = YoutubedlDownloadJob
				.builder()
				.withUrl(url)
				.withOutputFilename(filename)
				.withName(url);
		return jobRepository.create(jobBuilder);
	}

	public void startDownload(final DownloadJobHandle handle) {
		DownloadJob job = jobRepository.get(handle);
		downloadJobRunner.runJob(job);
	}
	
	public void cancelDownload(final DownloadJobHandle handle){
		DownloadJob job = jobRepository.get(handle);
		downloadJobRunner.cancelJob(job);
	}

	public void removeDownload(DownloadJobHandle downloadJobHandle) {
		DownloadJob downloadJob = this.jobRepository.get(downloadJobHandle);
		if(RUNNING.equals(downloadJob.getState())){
			cancelDownload(downloadJobHandle);
		}
		this.jobRepository.remove(downloadJobHandle);
	}

	public void restartDownload(DownloadJobHandle downloadJobHandle) {
		DownloadJob downloadJob = this.jobRepository.get(downloadJobHandle);
		if(RUNNING.equals(downloadJob.getState())){
			cancelDownload(downloadJobHandle);
		}
		startDownload(downloadJobHandle);
	}

	public Iterable<DownloadJob> listDownloads(){
		return this.jobRepository.list();
	}

	public Stream<DownloadJob> streamDownloads(){
		return this.jobRepository.stream();
	}

	public DownloadJob getDownload(DownloadJobHandle downloadJobHandle) {
		return this.jobRepository.get(downloadJobHandle);
	}

	public Stream<DownloadJob> streamFinishedDownloads() {
		return this.jobRepository.streamByJobState(FINISHED);
	}

	public Stream<DownloadJob> streamFailedDownloads() {
		return this.jobRepository.streamByJobState(ERROR);
	}

	public Stream<DownloadJob> streamRunningDownloads() {
		return this.jobRepository.streamByJobState(RUNNING);
	}

	public Stream<DownloadJob> streamWaitingDownloads() {
		return this.jobRepository.streamByJobState(WAITING);
	}

	private String resolveFilename(final String url) {
		String path = URI.create(url).getPath();
		return path.substring(path.lastIndexOf('/') + 1).toString();
	}



}
