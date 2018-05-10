package io.github.lumue.getdown.core.download.task;

import java.io.Serializable;

public class DownloadFormat implements Serializable {
	
	
	
	public String getFormatId() {
		return formatId;
	}
	
	private final String type;
	
	private final String formatId;
	
	private final Long expectedSize;
	
	private final String url;
	
	public DownloadFormat(String type, String formatId, Long expectedSize, String url) {
		this.type = type;
		this.formatId = formatId;
		this.expectedSize = expectedSize;
		this.url = url;
	}
	
	public String getType() {
		return type;
	}
	
	public Long getExpectedSize() {
		return expectedSize;
	}
	
	public String getUrl() {
		return url;
	}
}
