package io.github.lumue.getdown.download;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public interface ContentDownloader {

	public enum DownloadState {
		PENDING, RUNNING, PAUSED, CANCELLED, ERROR, FINISHED;
	}

	public interface DownloadProgress {

		public URI getUrl();

		public Long getSize();

		public long getDownloadedSize();

		public DownloadState getState();

		public Throwable getError();

	}

	/**
	 * start download from @param url to @param targetStream
	 * 
	 * @param url
	 * @param targetStream
	 * @return a DownloaderStatus, maybe updated during download with
	 *         asynchronous downloaders
	 * @throws IOException
	 */
	public DownloadProgress downloadContent(URI url, OutputStream targetStream) throws IOException;
}
