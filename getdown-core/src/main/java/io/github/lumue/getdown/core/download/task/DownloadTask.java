package io.github.lumue.getdown.core.download.task;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * a request to download something to somewgere
 *
 * Created by lm on 06.12.16.
 */
public class DownloadTask implements HasIdentity<String>, Serializable{

	private final String id;

	private final String sourceUrl;

	private final LocalDateTime creationTime;

	private String targetLocation;

	private Long priority =0L;


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

	private DownloadTask(DownloadTaskBuilder builder) {
		id = builder.id;
		sourceUrl = builder.sourceUrl;
		creationTime = LocalDateTime.now();
		setTargetLocation(builder.targetLocation);
		setPriority(builder.priority);
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

	@Override
	public String getHandle() {
		return getId();
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

	public static final class DownloadTaskBuilder implements ObjectBuilder<DownloadTask> {
		private String id;
		private String sourceUrl;
		private String targetLocation;
		private Long priority=100L;

		private DownloadTaskBuilder() {
		}

		public DownloadTaskBuilder withId(String val) {
			id = val;
			return this;
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
		public DownloadTaskBuilder withKey(String keyValue) {
			this.id=keyValue;
			return this;
		}

		public DownloadTask build() {
			return new DownloadTask(this);
		}
	}
}
