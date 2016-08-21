package io.github.lumue.getdown.core.download.job;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

	@JsonProperty("state")
	private DownloadState state = DownloadState.WAITING;
	@JsonProperty("size")
	private Long size = 1L; // null = unknown
	@JsonProperty("downloadedSize")
	private Long downloadedSize = 0L;
	@JsonProperty("error")
	private Throwable error;
	@JsonProperty("id")
	private final String id = UUID.randomUUID().toString();


	@JsonCreator
	public DownloadProgress(
			@JsonProperty("state") DownloadState state,
			@JsonProperty("size") Long size,
			@JsonProperty("downloadedSize") Long downloadedSize,
			@JsonProperty("error") Throwable error) {
		this.state = state;
		this.size = size;
		this.downloadedSize = downloadedSize;
		this.error = error;
	}

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