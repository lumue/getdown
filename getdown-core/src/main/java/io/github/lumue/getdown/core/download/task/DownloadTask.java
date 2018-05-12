package io.github.lumue.getdown.core.download.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
	
	private final List<DownloadFormat> availableFormats= new ArrayList<>();
	
	private final List<DownloadFormat> selectedFormats= new ArrayList<>();
	private String ext;
	private String infoJsonString;
	
	
	@JsonCreator
	private DownloadTask(
			@JsonProperty("handle") String handle,
			@JsonProperty("name") String name,
			@JsonProperty("sourceUrl") String sourceUrl,
			@JsonProperty("creationTime") LocalDateTime creationTime,
			@JsonProperty("state") TaskState state,
			@JsonProperty("targetLocation") String targetLocation,
			@JsonProperty("lastValidation") LocalDateTime lastValidation,
			@JsonProperty("expectedSize") Long expectedSize,
			@JsonProperty("priority") Long priority,
			@JsonProperty("availableFormats") List<DownloadFormat> availableFormats,
			@JsonProperty("selectedFormats") List<DownloadFormat> selectedFormats,
			@JsonProperty("ext") String ext) {
		this(state, handle, sourceUrl, creationTime);
		this.targetLocation = targetLocation;
		this.lastValidation=lastValidation;
		this.expectedSize=expectedSize;
		this.priority=priority;
		this.ext=ext;
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
		if(StringUtils.isEmpty(name))
			return getSourceUrl();
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
		this.availableFormats.clear();
		this.availableFormats.addAll(formats);
	}
	
	
	public void setSelectedFormats(List<DownloadFormat> downloadFormats) {
		this.selectedFormats.clear();
		this.selectedFormats.addAll(downloadFormats);
	}
	
	public List<DownloadFormat> getAvailableFormats() {
		return availableFormats;
	}
	
	public List<DownloadFormat> getSelectedFormats() {
		return selectedFormats;
	}
	
	public void setTargetExtension(String ext) {
		this.ext=ext;
	}
	
	public String getExt() {
		return ext;
	}
	
	public void setInfoJsonString(String infoJsonString) {
		this.infoJsonString = infoJsonString;
	}
	
	public String getInfoJsonString() {
		return infoJsonString;
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
