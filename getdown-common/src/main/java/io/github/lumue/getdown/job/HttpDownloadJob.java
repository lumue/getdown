package io.github.lumue.getdown.job;

import io.github.lumue.getdown.downloader.ContentDownloader;
import io.github.lumue.getdown.downloader.HttpContentDownloader;
import io.github.lumue.getdown.job.DownloadJob.AbstractDownloadJob;
import io.github.lumue.getdown.resolver.ContentLocation;
import io.github.lumue.getdown.resolver.ContentLocationResolver;
import io.github.lumue.getdown.resolver.ContentLocationResolverRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpDownloadJob extends AbstractDownloadJob {

	private static Logger LOGGER = LoggerFactory.getLogger(HttpDownloadJob.class);

	private ContentDownloader downloader = new HttpContentDownloader();

	private HttpDownloadJob(String url, String outputFilename) {
		super(url, outputFilename);
	}

	@Override
	public DownloadJobProgress run(String downloadPath, ContentLocationResolverRegistry contentLocationResolverRegistry) {
		try {
			LOGGER.debug("start download for url " + getUrl());

			ContentLocation contentLocation = createContentLocation(contentLocationResolverRegistry);

			OutputStream outStream = new FileOutputStream(downloadPath + File.separator
					+ contentLocation.getFilename());
			downloader.downloadContent(URI.create(contentLocation.getUrl()), outStream, null);
			outStream.flush();
			outStream.close();
			LOGGER.debug("finished download for url " + getUrl());

		} catch (IOException e) {
			getProgress().error(e);
			LOGGER.error("Error running Job " + this + " :", e);
		}

		return this.getProgress();
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
