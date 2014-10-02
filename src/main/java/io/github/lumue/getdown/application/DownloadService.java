package io.github.lumue.getdown.application;

import io.github.lumue.getdown.application.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.downloader.BasicContentDownloader;
import io.github.lumue.getdown.downloader.ContentDownloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * manage the execution of downloads
 * 
 * @author lm
 *
 */
public class DownloadService {

	public enum DownloadServiceState {
		RUNNING, STOPPED
	}

	private DownloadServiceState serviceState = DownloadServiceState.STOPPED;

	ExecutorService executorService = Executors.newCachedThreadPool();



	private DownloadJobQueue jobQueue = new VolatileDownloadJobQueue();

	private DownloadJobRepository jobRepository = new VolatileDownloadJobRepository();

	private ContentDownloader downloader = new BasicContentDownloader();

	public DownloadJobHandle queueDownload(String url) {
		return null;
	}

	DownloadJobQueue getJobQueue() {
		return jobQueue;
	}

	public void start() {

		serviceState = DownloadServiceState.RUNNING;

		while (DownloadServiceState.RUNNING.equals(serviceState)) {

			while (jobQueue.hasNextJob()) {
				DownloadJob job = jobQueue.pollJob();
				if (job != null)
					executeJob(job);
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}

	}

	public void shutdown() {
		serviceState = DownloadServiceState.STOPPED;
	}

	private void executeJob(DownloadJob job) {
		executorService.submit(new Runnable() {
			
			@Override
			public void run() {
				runDownloadJob(job);
			}
		});
	}

	private void runDownloadJob(DownloadJob job) {
		ContentDownloader downloader = getDownloader();
		try {
			final OutputStream targetStream = new FileOutputStream(job.getOutputFilename());
			downloader.downloadContent(URI.create(job.getUrl()), targetStream, job.getProgressListener());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ContentDownloader getDownloader() {
		return downloader;
	}
}
