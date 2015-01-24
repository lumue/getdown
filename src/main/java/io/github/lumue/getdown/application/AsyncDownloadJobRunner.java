package io.github.lumue.getdown.application;

import io.github.lumue.getdown.downloader.BasicContentDownloader;
import io.github.lumue.getdown.downloader.ContentDownloader;
import io.github.lumue.getdown.downloader.ContentDownloader.DownloadState;
import io.github.lumue.getdown.downloader.DownloadProgressListener;
import io.github.lumue.getdown.scraper.StreamcloudUrlScraper;
import io.github.lumue.getdown.scraper.UrlScraper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AsyncDownloadJobRunner implements DownloadJobRunner {

	private final ExecutorService executorService;

	private ContentDownloader downloader = new BasicContentDownloader();

	private UrlScraper scraper = new StreamcloudUrlScraper();

	private static Logger LOGGER = LoggerFactory.getLogger(AsyncDownloadJobRunner.class);

	@Autowired
	AsyncDownloadJobRunner(ExecutorService executorService) {
		super();
		this.executorService = executorService;
	}


	@Override
	public void runJob(final DownloadJob job) {
		this.executorService.execute(new Runnable() {

			@Override
			public void run() {
				try {

					OutputStream outStream = new FileOutputStream(job.getOutputFilename());

					downloader.downloadContent(URI.create(job.getUrl()), outStream, job.getProgressListener());

					DownloadProgressListener progress = job.getProgressListener();
					while (!DownloadState.FINISHED.equals(progress.getState())) {
						Thread.sleep(500);
					}

					outStream.flush();
					outStream.close();

				} catch (IOException | InterruptedException e) {
					job.getProgressListener().error(e);
					LOGGER.error("Error running Job " + job + " :", e);
				}
			}
		});

	}

}
