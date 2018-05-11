package io.github.lumue.getdown.core.download.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DownloadFormat implements Serializable {
	
	
	private static final long serialVersionUID = -1584266471925381L;
	
	
	
	public enum Type{AUDIO,VIDEO,MERGED};
	
	public String getFormatId() {
		return formatId;
	}
	
	private final Type type;
	
	private final String codec;
	private final String filenameExtension;
	
	private final String formatId;
	
	private final Long expectedSize;
	
	private final String url;
	private final Map<String, String> httpHeaders=new HashMap<>();
	
	@JsonCreator
	public DownloadFormat(@JsonProperty("type") Type type,
	                      @JsonProperty("formatId") String formatId,
	                      @JsonProperty("expectedSize") Long expectedSize,
	                      @JsonProperty("url") String url,
	                      @JsonProperty("httpHeaders") Map<String, String> httpHeaders,
	                      @JsonProperty("codec") String codec,
	                      @JsonProperty("filenameExtension") String filenameExtension) {
		this.type = type;
		this.formatId = formatId;
		this.expectedSize = expectedSize;
		this.url = url;
		this.codec = codec;
		this.filenameExtension = filenameExtension;
		this.httpHeaders.putAll(httpHeaders);
	}
	
	public Type getType() {
		return type;
	}
	
	public Long getExpectedSize() {
		return expectedSize;
	}
	
	public String getUrl() {
		return url;
	}
	
	public Map<String, String> getHttpHeaders() {
		return httpHeaders;
	}
	
	public String getFilename() {
		return getType().name()+filenameExtension;
	}
	
	public String getCodec() {
		return codec;
	}
}
