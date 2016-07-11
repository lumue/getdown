package io.github.lumue.getdown.core.download.job;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.bus.Event;
import reactor.bus.EventBus;

public class AsyncDownloadJobRunner {

	private final ScheduledThreadPoolExecutor downloadExecutor;

	private final ScheduledThreadPoolExecutor prepareExecutor;

	private final Integer maxThreadsPrepare;

	private final Integer maxThreadsDownload;






	private static Logger LOGGER = LoggerFactory.getLogger(AsyncDownloadJobRunner.class);


	public AsyncDownloadJobRunner(
			int maxThreadsDownload,
			int maxThreadsPrepare) {
		super();
		this.maxThreadsDownload=maxThreadsDownload;
		this.maxThreadsPrepare=maxThreadsPrepare;
		this.downloadExecutor = executor(maxThreadsDownload);
		this.prepareExecutor  =executor(maxThreadsPrepare);
	}



	public void runJob(final DownloadJob job) {
		String jobUrl = job.getUrl();
		AsyncDownloadJobRunner.LOGGER.debug("starting " + jobUrl);


		Download.DownloadJobState jobState = job.getState();
		if(!Download.DownloadJobState.PREPARED.equals(jobState)
			&&!Download.DownloadJobState.PREPARING.equals(jobState)){
			AsyncDownloadJobRunner.LOGGER.debug("submitting " + jobUrl +" for prepare");
			this.prepareExecutor.submit(job::prepare);
		}

		AsyncDownloadJobRunner.LOGGER.debug("submitting " + jobUrl +" for download");
		this.downloadExecutor.submit(()-> {
			while (Download.DownloadJobState.PREPARING.equals(jobState)) {
				try {
					LOGGER.debug("waiting for prepare of "+ jobUrl +" to finish before starting download ");
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			job.run();
		});
	}



	public void cancelJob(DownloadJob job) {
		job.cancel();
	}

	private static ScheduledThreadPoolExecutor executor(Integer threads){
		return (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(threads);

	}

}
