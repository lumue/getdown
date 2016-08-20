package io.github.lumue.getdown.core.download.job;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.downloader.youtubedl.YoutubedlDownloadJob;
import io.github.lumue.getdown.core.download.job.Download.DownloadJobHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.bus.Event;
import reactor.bus.EventBus;

import static io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobState.*;

/**
 * manage the execution of downloads
 * 
 * @author lm
 *
 */
public class DownloadService {

	private final static Logger LOGGER=LoggerFactory.getLogger(DownloadService.class);

	private final DownloadJobRepository jobRepository;

	private final AsyncDownloadJobRunner downloadJobRunner;

	private final String downloadPath;

	private final EventBus eventbus;

	
	public DownloadService(DownloadJobRepository jobRepository,
	                       AsyncDownloadJobRunner downloadJobRunner,
	                       String downloadPath,
	                       EventBus eventbus) {
		super();
		this.jobRepository = jobRepository;
		this.downloadJobRunner = downloadJobRunner;
		this.downloadPath = downloadPath;
		this.eventbus = eventbus;
	}

	public DownloadJob addDownload(final String url) {
		String filename = resolveFilename(url);
		ObjectBuilder<DownloadJob> jobBuilder = YoutubedlDownloadJob
				.builder()
				.withUrl(url)
				.withOutputFilename(filename)
				.withName(url)
				.withDownloadPath(downloadPath);

		DownloadJob job = jobRepository.create(jobBuilder);
		job.addObserver( o ->	eventbus.notify("downloads", Event.wrap(Objects.requireNonNull(o))));
		eventbus.notify("downloads", Event.wrap(Objects.requireNonNull(job)));
		return job;
	}

	public void startDownload(final DownloadJobHandle handle) {
		DownloadJob job = jobRepository.get(handle);
		downloadJobRunner.runJob(job);
	}
	
	public void cancelDownload(final DownloadJobHandle handle){
		DownloadJob job = jobRepository.get(handle);
		if(job==null){
			LOGGER.warn("no job with handle "+handle+" found. nothing to cancel");
			return;
		}

		downloadJobRunner.cancelJob(job);
	}

	public void removeDownload(DownloadJobHandle downloadJobHandle) {
		DownloadJob downloadJob = this.jobRepository.get(downloadJobHandle);

		if(downloadJob==null){
			LOGGER.warn("no job with handle "+downloadJobHandle+" found. nothing to remove");
			return;
		}

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
