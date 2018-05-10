package io.github.lumue.getdown.core.download.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.util.Observable;
import io.github.lumue.getdown.core.common.util.Observer;
import io.github.lumue.getdown.core.download.job.Download;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * a request to download something to somewhere
 * <p>
 * Created by lm on 06.12.16.
 */
public class DownloadTask implements HasIdentity<String>, Serializable {
	
	
	private static final long serialVersionUID = 1651793106903747882L;
	
	private TaskState state;
	
	private final String handle;
	
	private final String sourceUrl;
	
	private final LocalDateTime creationTime;
	
	private LocalDateTime lastValidation=null;
	
	private String targetLocation;
	
	private Long expectedSize;
	
	private String name;
	
	private Long priority = 0L;
	
	private List<DownloadFormat> availableFormats;
	private List<DownloadFormat> selectedFormats;
	
	
	@JsonCreator
	private DownloadTask(
			@JsonProperty("handle") String handle,
			@JsonProperty("sourceUrl") String sourceUrl,
			@JsonProperty("creationTime") LocalDateTime creationTime,
			TaskState state, @JsonProperty("targetLocation") String targetLocation) {
		this(state, handle, sourceUrl, creationTime);
		this.targetLocation = targetLocation;
	}
	
	private DownloadTask(TaskState state, String handle, String sourceUrl, LocalDateTime creationTime) {
		this.state = state;
		this.handle = handle;
		this.creationTime = creationTime != null ? creationTime : LocalDateTime.now();
		this.sourceUrl = sourceUrl;
	}
	
	private DownloadTask(DownloadTaskBuilder builder) {
		this.state = TaskState.SUBMITTED;
		handle = builder.id;
		sourceUrl = builder.sourceUrl;
		creationTime = LocalDateTime.now();
		setTargetLocation(builder.targetLocation);
		setPriority(builder.priority);
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
	
	@Override
	public String getHandle() {
		return handle;
	}
	
	public void setPriority(Long priority) {
		this.priority = priority;
	}
	
	public Long getPriority() {
		return this.priority;
	}
	
	
	public static DownloadTaskBuilder builder() {
		return new DownloadTaskBuilder();
	}
	
	public DownloadTaskBuilder copy() {
		return builder().withHandle(getHandle())
				.withPriority(getPriority())
				.withSourceUrl(getSourceUrl())
				.withTargetLocation(getTargetLocation());
	}
	
	public Long getExpectedSize() {
		return expectedSize;
	}
	
	public void setExpectedSize(Long expectedSize) {
		this.expectedSize = expectedSize;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void validating() {
		this.state = TaskState.VALIDATING;
	}
	
	
	public TaskState getState() {
		return state;
	}
	
	public void validated() {
		state = TaskState.VALIDATED;
		lastValidation=LocalDateTime.now();
	}
	
	public void invalid() {
		state = TaskState.INVALID;
		lastValidation=LocalDateTime.now();
	}
	
	public LocalDateTime getLastValidation() {
		return lastValidation;
	}
	
	public void setAvailableFormats(List<DownloadFormat> formats) {
		this.availableFormats=formats;
	}
	
	public void setFormat(DownloadFormat requestedFormat) {
	}
	
	public void setSelectedFormats(List<DownloadFormat> downloadFormats) {
		this.selectedFormats=downloadFormats;
	}
	
	
	public static final class DownloadTaskBuilder implements ObjectBuilder<DownloadTask> {
		private String id;
		private String sourceUrl;
		private String targetLocation;
		private Long priority = 100L;
		
		private DownloadTaskBuilder() {
		}
		
		
		public DownloadTaskBuilder withSourceUrl(String val) {
			sourceUrl = val;
			return this;
		}
		
		
		public DownloadTaskBuilder withTargetLocation(String val) {
			targetLocation = val;
			return this;
		}
		
		public DownloadTaskBuilder withPriority(Long val) {
			priority = val;
			return this;
		}
		
		@Override
		public DownloadTaskBuilder withHandle(String keyValue) {
			this.id = keyValue;
			return this;
		}
		
		public DownloadTask build() {
			return new DownloadTask(this);
		}
		
		@Override
		public boolean hasHandle() {
			return !StringUtils.isEmpty(this.id);
		}
	}
}
