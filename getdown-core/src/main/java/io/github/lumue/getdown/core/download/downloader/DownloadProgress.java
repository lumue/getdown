package io.github.lumue.getdown.core.download.downloader;

import io.github.lumue.getdown.core.download.downloader.ContentDownloader.DownloadState;

import java.util.UUID;

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

	void start() {
		this.state = DownloadState.DOWNLOADING;
	}

	void finish() {
		this.state = DownloadState.FINISHED;
	}

	void increaseDownloadedSize(long value) {
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


	void setSize(Long size) {
		this.size = size;
	}


}