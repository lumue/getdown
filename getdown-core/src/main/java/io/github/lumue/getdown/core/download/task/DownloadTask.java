package io.github.lumue.getdown.core.download.task;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.DateTimeException;
import java.time.LocalDateTime;


/**
 * a request to download something to somewgere
 *
 * Created by lm on 06.12.16.
 */
public class DownloadTask {

	private final String id;

	private final String sourceUrl;

	private final LocalDateTime creationTime;

	private String targetLocation;

	public DownloadTask(String id, String sourceUrl) {
		this(id,sourceUrl,LocalDateTime.now());
	}

	@JsonCreator
	private DownloadTask(
			String id,
			String sourceUrl,
			LocalDateTime creationTime,
			String targetLocation) {
		this(id, sourceUrl,creationTime);
		this.targetLocation = targetLocation;
	}

	private DownloadTask(String id, String sourceUrl, LocalDateTime creationTime) {
		this.id=id;
		this.creationTime=creationTime;
		this.sourceUrl=sourceUrl;
	}

	public String getId() {
		return id;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public String getTargetLocation() {
		return targetLocation;
	}

	public void setTargetLocation(String targetLocation) {
		this.targetLocation = targetLocation;
	}
}
