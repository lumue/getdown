package io.github.lumue.getdown.core.download.downloader.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import io.github.lumue.getdown.core.download.job.ContentLocation;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lumue.getdown.core.download.job.Download;

public class HttpDownloadJob extends Download implements DownloadJob{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7225589581609982485L;

	private static Logger LOGGER = LoggerFactory
			.getLogger(HttpDownloadJob.class);

	private final static ContentDownloader DOWNLOADER = new HttpContentDownloader();
	private Long index=System.currentTimeMillis();

	private HttpDownloadJob(String name,String url, String outputFilename,String host) {
		super(name, url, outputFilename,host);
	}

	@Override
	public void prepare() {

	}

	@Override
	public void run() {
		OutputStream outStream = null;
		try {
			LOGGER.debug("start download for url " + getUrl());
			start();


			if(DownloadJobState.ERROR.equals(getState()))
				return;
			
			download();
			
			ContentLocation contentLocation=getContentLocation().get();
			
			outStream = new FileOutputStream(getDownloadPath()+ File.separator
					+ contentLocation.getFilename());
			DOWNLOADER.downloadContent(URI.create(contentLocation.getUrl()),
					outStream,

			downloadProgress -> {
				progress( downloadProgress);
			} );

			outStream.flush();

			finish();

			LOGGER.debug("finished download for url " + getUrl());

		} catch (Throwable e) {
			error( e);
			LOGGER.error("Error running Job " + this + " :", e);
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static class HttpDownloadJobBuilder
			extends DownloadBuilder {

		@Override
		public DownloadJob build() {
			return new HttpDownloadJob(this.name,this.url, this.outputFilename,this.host);
		}

	}


	@Override
	public void cancel() {
		doObserved(()->
		{
			getDownloadProgress().ifPresent(progress -> {progress.cancel();});
			downloadJobState = DownloadJobState.CANCELLED;
		}); 
	}

	@Override
	public Long getIndex() {
		return index;
	}

	@Override
	public boolean isPrepared() {
		return false;
	}

}
