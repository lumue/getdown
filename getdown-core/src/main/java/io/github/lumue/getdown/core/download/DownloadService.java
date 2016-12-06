package io.github.lumue.getdown.core.download;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.downloader.youtubedl.YoutubedlDownloadJob;
import io.github.lumue.getdown.core.download.job.AsyncDownloadJobRunner;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;
import io.github.lumue.getdown.core.download.job.UrlProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.bus.Event;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;

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

	private final UrlProcessor urlProcessor;

	
	public DownloadService(DownloadJobRepository jobRepository,
	                       AsyncDownloadJobRunner downloadJobRunner,
	                       String downloadPath,
	                       EventBus eventbus, UrlProcessor urlProcessor) {
		super();
		this.jobRepository = jobRepository;
		this.downloadJobRunner = downloadJobRunner;
		this.downloadPath = downloadPath;
		this.eventbus = eventbus;
		this.urlProcessor = urlProcessor;

	}

	public DownloadJob addDownload(final String url) {
		String processedUrl=preprocessUrl(url);
		String filename = resolveFilename(processedUrl);
		ObjectBuilder<DownloadJob> jobBuilder = YoutubedlDownloadJob
				.builder()
				.withUrl(processedUrl)
				.withOutputFilename(filename)
				.withName(processedUrl)
				.withDownloadPath(downloadPath);

		DownloadJob job = jobRepository.create(jobBuilder);
		job.addObserver( o ->
				eventbus.notify("downloads", Event.wrap(Objects.requireNonNull(o))
				));
		eventbus.notify("downloads", Event.wrap(Objects.requireNonNull(job)));
		return job;
	}

	private String preprocessUrl(String url) {
		return urlProcessor.processUrl(url);
	}


	public void startDownload(final String handle) {
		DownloadJob job = getObservedDownloadJob(handle);
		downloadJobRunner.submitJob(job);
	}
	
	public void cancelDownload(final String handle){
		DownloadJob job = getObservedDownloadJob(handle);
		if(job==null){
			LOGGER.warn("no job with handle "+handle+" found. nothing to cancel");
			return;
		}

		downloadJobRunner.cancelJob(job);
	}

	public void removeDownload(String downloadJobHandle) {
		DownloadJob downloadJob = getObservedDownloadJob(downloadJobHandle);
		if(downloadJob==null){
			LOGGER.warn("no job with handle "+downloadJobHandle+" found. nothing to remove");
			return;
		}

		LOGGER.debug("removing download "+downloadJob);
		if(RUNNING.equals(downloadJob.getState())){
			cancelDownload(downloadJobHandle);
		}
		this.jobRepository.remove(downloadJobHandle);
	}

	private DownloadJob getObservedDownloadJob(String downloadJobHandle) {
		DownloadJob downloadJob = this.jobRepository.get(downloadJobHandle);
		downloadJob.addObserver( o ->	eventbus.notify("downloads", Event.wrap(Objects.requireNonNull(o))));
		return downloadJob;
	}

	public void restartDownload(String downloadJobHandle) {
		DownloadJob downloadJob = getObservedDownloadJob(downloadJobHandle);
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

	public DownloadJob getDownload(String downloadJobHandle) {
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
		return path.substring(path.lastIndexOf('/') + 1);
	}

	@PostConstruct
	public void resumeWaitingJobsFromRepo(){
		this.jobRepository.stream()
				.filter(job -> !job.getState().equals(FINISHED) && !job.getState().equals(CANCELLED))
				.forEach( job -> {
					job.addObserver( o ->	eventbus.notify("downloads", Event.wrap(Objects.requireNonNull(o))));
					downloadJobRunner.submitJob(job);
				});
	}

	public void removeAll() {
		LOGGER.debug("removing all downloads");
		this.jobRepository.stream()
				.forEach(job->removeDownload(job.getHandle()));
	}
}