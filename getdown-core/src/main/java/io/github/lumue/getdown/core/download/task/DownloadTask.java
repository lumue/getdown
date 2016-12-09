package io.github.lumue.getdown.core.download.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * a request to download something to somewhere
 *
 * Created by lm on 06.12.16.
 */
public class DownloadTask implements HasIdentity<String>, Serializable{


	private static final long serialVersionUID = 1651793106903747882L;

	private final String handle;

	private final String sourceUrl;

	private final LocalDateTime creationTime;

	private String targetLocation;

	private Long priority =0L;


	@JsonCreator
	private DownloadTask(
			@JsonProperty("handle") String handle,
			@JsonProperty("sourceUrl") String sourceUrl,
			@JsonProperty("creationTime") LocalDateTime creationTime,
			@JsonProperty("targetLocation") String targetLocation) {
		this(handle, sourceUrl,creationTime);
		this.targetLocation = targetLocation;
	}

	private DownloadTask(String handle, String sourceUrl, LocalDateTime creationTime) {
		this.handle = handle;
		this.creationTime=creationTime!=null?creationTime:LocalDateTime.now();
		this.sourceUrl=sourceUrl;
	}

	private DownloadTask(DownloadTaskBuilder builder) {
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

	public static final class DownloadTaskBuilder implements ObjectBuilder<DownloadTask> {
		private String id;
		private String sourceUrl;
		private String targetLocation;
		private Long priority=100L;

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
			this.id=keyValue;
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
