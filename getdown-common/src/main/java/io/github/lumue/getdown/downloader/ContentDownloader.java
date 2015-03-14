package io.github.lumue.getdown.downloader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public interface ContentDownloader {

	public enum DownloadState {
		DOWNLOADING, WAITING, CANCELLED, ERROR, FINISHED;
	}

	
	/**
	 * start download from
	 * 
	 * @param url
	 *            to
	 * @param targetStream
	 *            propagate state to
	 * @param downloadProgressListener
	 * 
	 *            if a {@link DownloadProgress} is provided, it should
	 *            be updated accordingly during downloads
	 * 
	 * @throws IOException
	 */
	public void downloadContent(URI url, OutputStream targetStream, DownloadProgress downloadProgressListener) throws IOException;
}
