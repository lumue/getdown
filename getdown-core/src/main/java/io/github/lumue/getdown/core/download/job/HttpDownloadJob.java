package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.download.downloader.ContentDownloader;
import io.github.lumue.getdown.core.download.downloader.HttpContentDownloader;
import io.github.lumue.getdown.core.download.job.DownloadJob.AbstractDownloadJob;
import io.github.lumue.getdown.core.download.resolver.ContentLocation;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolver;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolverRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class HttpDownloadJob extends AbstractDownloadJob {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3487833171703064174L;

	private static Logger LOGGER = LoggerFactory.getLogger(HttpDownloadJob.class);

	private final static ContentDownloader DOWNLOADER = new HttpContentDownloader();

	private HttpDownloadJob(String url, String outputFilename) {
		super(url, outputFilename);
	}

	@Override
	public void run(String downloadPath, ContentLocationResolverRegistry contentLocationResolverRegistry,
			DownloadJobProgressListener downloadJobProgressListener) {
		try {
			LOGGER.debug("start download for url " + getUrl());
			start(downloadJobProgressListener);

			resolve(downloadJobProgressListener);
			this.setContentLocation(createContentLocation(contentLocationResolverRegistry));
			ContentLocation contentLocation = this.getContentLocation().get();
			
			download(downloadJobProgressListener);
			OutputStream outStream = new FileOutputStream(downloadPath + File.separator
					+ contentLocation.getFilename());
			DOWNLOADER.downloadContent(URI.create(contentLocation.getUrl()), outStream,

					downloadProgress -> {

						getDownloadProgress().orElseGet(() -> {

							progress(downloadJobProgressListener,downloadProgress);
							return downloadProgress;

						});

						progress(downloadJobProgressListener,downloadProgress);

					});

			outStream.flush();
			outStream.close();
			
			finish(downloadJobProgressListener);
			
			LOGGER.debug("finished download for url " + getUrl());

		} catch (Throwable e) {
			error(downloadJobProgressListener,e);
			LOGGER.error("Error running Job " + this + " :", e);
		}
	}

	


	private ContentLocation createContentLocation(ContentLocationResolverRegistry contentLocationResolverRegistry) throws IOException {

		URI startUrl = URI.create(getUrl());
		LOGGER.debug("creating ContentLocation for " + startUrl.toString());

		String host = startUrl.getHost();
		ContentLocationResolver locationResolver = contentLocationResolverRegistry.lookup(host);
		ContentLocation contentLocation = null;

		if (locationResolver != null)
			contentLocation = locationResolver.resolve(startUrl.toString());
		else {
			LOGGER.warn("no suitable resolver for host. creating generic ContentLocation from url");
			contentLocation = new ContentLocation(getUrl(), getOutputFilename());
		}

		LOGGER.debug("created " + contentLocation + " for " + startUrl.toString());

		return contentLocation;
	}

	public static class HttpDownloadJobBuilder extends AbstractDownloadJobBuilder {

		@Override
		public DownloadJob build() {
			return new HttpDownloadJob(this.url, this.outputFilename);
		}

	}

	

	

}
