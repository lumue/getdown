package io.github.lumue.getdown.core.download.job;

import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncDownloadJobRunner {

	private final ScheduledThreadPoolExecutor downloadExecutor;

	private final ScheduledThreadPoolExecutor prepareExecutor;







	private static Logger LOGGER = LoggerFactory.getLogger(AsyncDownloadJobRunner.class);


	public AsyncDownloadJobRunner(
			int maxThreadsPrepare,
			int maxThreadsDownload) {
		super();
		this.downloadExecutor = executor(maxThreadsDownload);
		this.prepareExecutor  =executor(maxThreadsPrepare);
	}



	public void runJob(final DownloadJob job) {
		String jobUrl = job.getUrl();
		AsyncDownloadJobRunner.LOGGER.debug("starting " + jobUrl);


		DownloadJob.DownloadJobState jobState = job.getState();
		if(!job.isPrepared()){
			AsyncDownloadJobRunner.LOGGER.debug("submitting " + jobUrl +" for prepare");
			this.prepareExecutor.submit(job::prepare);
		}

		AsyncDownloadJobRunner.LOGGER.debug("submitting " + jobUrl +" for download");
		this.downloadExecutor.submit(()-> {
			while (!job.isPrepared()) {
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
