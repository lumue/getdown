package io.github.lumue.getdown.application;

import io.github.lumue.getdown.downloader.BasicContentDownloader;
import io.github.lumue.getdown.downloader.ContentDownloader;
import io.github.lumue.getdown.downloader.ContentDownloader.DownloadState;
import io.github.lumue.getdown.downloader.DownloadProgressListener;
import io.github.lumue.getdown.resolver.ContentLocation;
import io.github.lumue.getdown.resolver.ContentLocationResolver;
import io.github.lumue.getdown.resolver.ContentLocationResolverRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class AsyncDownloadJobRunner implements DownloadJobRunner {

	private final ExecutorService executorService;

	private final ContentLocationResolverRegistry contentLocationResolverRegistry;

	private final String downloadPath;

	private ContentDownloader downloader = new BasicContentDownloader();

	private static Logger LOGGER = LoggerFactory.getLogger(AsyncDownloadJobRunner.class);

	@Autowired
	AsyncDownloadJobRunner(ExecutorService executorService, ContentLocationResolverRegistry contentLocationResolverRegistry,
			@Value("${getdown.downloadPath}") String downloadPath) {
		super();
		this.executorService = executorService;
		this.contentLocationResolverRegistry = contentLocationResolverRegistry;
		this.downloadPath = downloadPath;
	}


	@Override
	public void runJob(final DownloadJob job) {
		this.executorService.execute(new Runnable() {

			@Override
			public void run() {
				try {

					AsyncDownloadJobRunner.LOGGER.debug("starting download for url " + job.getUrl());

					URI startUrl = URI.create(job.getUrl());
					ContentLocation contentLocation = createContentLocation(job, startUrl, job.getProgressListener());

					OutputStream outStream = new FileOutputStream(AsyncDownloadJobRunner.this.downloadPath + File.separator + contentLocation.getFilename());
					downloader.downloadContent(URI.create(contentLocation.getUrl()), outStream, job.getProgressListener());

					DownloadProgressListener progress = job.getProgressListener();
					while (!DownloadState.FINISHED.equals(progress.getState())) {
						Thread.sleep(500);
					}

					outStream.flush();
					outStream.close();
					AsyncDownloadJobRunner.LOGGER.debug("finished download for url " + job.getUrl());

				} catch (IOException | InterruptedException e) {
					job.getProgressListener().error(e);
					LOGGER.error("Error running Job " + job + " :", e);
				}
			}

			private ContentLocation createContentLocation(final DownloadJob job, URI startUrl,
					DownloadProgressListener downloadProgressListener) throws IOException {
				LOGGER.debug("creating ContentLocation for " + startUrl.toString());

				if (downloadProgressListener != null)
					downloadProgressListener.resolveContentLocation();

				String host = startUrl.getHost();
				ContentLocationResolver locationResolver = contentLocationResolverRegistry.lookup(host);
				ContentLocation contentLocation = null;

				if (locationResolver != null)
					contentLocation = locationResolver.resolve(startUrl.toString());
				else {
					LOGGER.warn("no suitable resolver for host. creating generic ContentLocation from url");
					contentLocation = new ContentLocation(job.getUrl(), job.getOutputFilename());
				}

				LOGGER.debug("created " + contentLocation + " for " + startUrl.toString());

				return contentLocation;
			}
		});

	}

}
