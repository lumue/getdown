package io.github.lumue.getdown.core.download.job;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty("startedAt")
	private LocalDateTime startedAt;
	@JsonProperty("finishedAt")
	private LocalDateTime finishedAt;
	@JsonProperty("updatedAt")
	private LocalDateTime updatedAt;

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
		this.startedAt= LocalDateTime.now();
		this.updatedAt=LocalDateTime.now();
	}

	public void finish() {
		this.state = DownloadState.FINISHED;
		this.finishedAt=LocalDateTime.now();
		this.updatedAt=LocalDateTime.now();
	}
	
	public void cancel() {
		this.state = DownloadState.CANCELLED;
		this.updatedAt=LocalDateTime.now();
	}

	public void increaseDownloadedSize(long value) {
		this.downloadedSize += value;
		this.updatedAt=LocalDateTime.now();
	}


	public void error(Throwable t) {
		this.error = t;
		this.state = DownloadState.ERROR;
		this.updatedAt=LocalDateTime.now();
	}


	public Throwable getError() {
		return error;
	}


	public String getId() {
		return id;
	}


	public void setSize(Long size) {
		this.size = size;
		this.updatedAt=LocalDateTime.now();
	}
	


	public void updateDownloadedSize(Long downloadedSize) {
		this.downloadedSize = downloadedSize;
		this.updatedAt=LocalDateTime.now();
	}
}