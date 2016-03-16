package io.github.lumue.getdown.core.download.job;

import java.util.UUID;

import io.github.lumue.getdown.core.download.downloader.internal.ContentDownloader.DownloadState;

/**
 * handle to an active download
 * 
 * @author lm
 *
 */
public class DownloadProgress {

	public DownloadProgress() {
		super();
	}

	private DownloadState state = DownloadState.WAITING;
	private Long size = 1L; // null = unknown
	private long downloadedSize = 0;
	private Throwable error;
	private final String id = UUID.randomUUID().toString();


	public Long getSize() {
		return size;
	}


	public long getDownloadedSize() {
		return downloadedSize;
	}



	public DownloadState getState() {
		return state;
	}

	public void start() {
		this.state = DownloadState.DOWNLOADING;
	}

	public void finish() {
		this.state = DownloadState.FINISHED;
	}
	
	public void cancel() {
		this.state = DownloadState.CANCELLED;
	}

	public void increaseDownloadedSize(long value) {
		this.downloadedSize += value;
	}


	public void error(Throwable t) {
		this.error = t;
		this.state = DownloadState.ERROR;
	}


	public Throwable getError() {
		return error;
	}


	public String getId() {
		return id;
	}


	public void setSize(Long size) {
		this.size = size;
	}


	public void updateDownloadedSize(Long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}
}