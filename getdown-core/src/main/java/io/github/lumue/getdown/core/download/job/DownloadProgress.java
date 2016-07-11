package io.github.lumue.getdown.core.download.job;

import java.io.Serializable;
import java.util.UUID;

import io.github.lumue.getdown.core.download.downloader.internal.ContentDownloader.DownloadState;

/**
 * handle to an active download
 * 
 * @author lm
 *
 */
public class DownloadProgress implements Serializable{

	public DownloadProgress() {
		super();
	}

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





	public void increaseDownloadedSize(long value) {
		this.downloadedSize += value;
	}


	public void error(Throwable t) {
		this.error = t;
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