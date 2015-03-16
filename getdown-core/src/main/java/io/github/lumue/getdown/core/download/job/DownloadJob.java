package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.downloader.DownloadProgress;
import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolverRegistry;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface DownloadJob extends HasIdentity<DownloadJobHandle> {

	@Override
	public DownloadJobHandle getHandle();

	public DownloadJobProgress getProgress();

	public String getOutputFilename();

	public String getUrl();

	public DownloadJobProgress run(String downloadPath, ContentLocationResolverRegistry contentLocationResolverRegistry,
			DownloadJobProgressListener progressListener);

	@FunctionalInterface
	public interface DownloadJobProgressListener {
		public void onChange(DownloadJobProgress downloadProgress);
	}

	static class DownloadJobHandle implements Serializable {

		private static final long serialVersionUID = -7582907691952041979L;

		@JsonProperty("key")
		private String key;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		DownloadJobHandle() {
			this(UUID.randomUUID().toString());
		}

		@JsonCreator
		public DownloadJobHandle(@JsonProperty("key") String key) {
			this.key = key;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DownloadJobHandle other = (DownloadJobHandle) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return key;
		}

	}




	public static class DownloadJobProgress {

		public static enum DownloadJobState {
			WAITING, RUNNING, ERROR, FINISHED;
		}

		private DownloadJobState downloadJobState = DownloadJobState.WAITING;

		private Optional<DownloadProgress> downloadProgress = Optional.empty();

		private Optional<String> message = Optional.empty();

		private Optional<Throwable> error = Optional.empty();

		public DownloadJobState getState() {
			return downloadJobState;
		}

		public void error(Exception e) {
			downloadJobState = DownloadJobState.ERROR;
			message = Optional.of(e.getLocalizedMessage());
		}

		public DownloadJobState getDownloadJobState() {
			return downloadJobState;
		}

		public Optional<DownloadProgress> getDownloadProgress() {
			return downloadProgress;
		}

		public Optional<String> getMessage() {
			return message;
		}

		public Optional<Throwable> getError() {
			return error;
		}

		public void setDownloadProgress(DownloadProgress downloadProgress) {
			this.downloadProgress = Optional.of(downloadProgress);
		}

		public void start() {
			downloadJobState = DownloadJobState.RUNNING;
			message = Optional.of("initializing...");
		}

		public void resolve(String url) {
			message = Optional.of("resolving " + url);
		}

		public void download(String url) {
			message = Optional.of("downloading from " + url);
		}

		public void finish() {
			downloadJobState = DownloadJobState.FINISHED;
			message = Optional.of("");
		}

	}

	public abstract static class AbstractDownloadJobBuilder implements ObjectBuilder<DownloadJob> {
		String outputFilename;
		String url;

		public AbstractDownloadJobBuilder withOutputFilename(String outputFilename) {
			this.outputFilename = outputFilename;
			return this;
		}

		public AbstractDownloadJobBuilder withUrl(String url) {
			this.url = url;
			return this;
		}
	}

	public abstract class AbstractDownloadJob implements DownloadJob, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3831495614060514606L;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((handle == null) ? 0 : handle.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AbstractDownloadJob other = (AbstractDownloadJob) obj;
			if (handle == null) {
				if (other.handle != null)
					return false;
			} else if (!handle.equals(other.handle))
				return false;
			return true;
		}

		@JsonProperty("handle")
		private final DownloadJobHandle handle;
		@JsonProperty("progressListener")
		private final DownloadJobProgress progress;
		@JsonProperty("outputFilename")
		private final String outputFilename;
		@JsonProperty("url")
		private final String url;

		@JsonCreator
		private AbstractDownloadJob(
				@JsonProperty("url") String url,
				@JsonProperty("outputFilename") String outputFilename,
				@JsonProperty("progress") DownloadJobProgress progress,
				@JsonProperty("handle") DownloadJobHandle handle) {
			super();
			this.progress = progress;
			this.outputFilename = outputFilename;
			this.url = url;
			this.handle = handle;
		}

		public AbstractDownloadJob(
				String url,
				String outputFilename) {
			super();
			this.progress = new DownloadJobProgress();
			this.outputFilename = outputFilename;
			this.url = url;
			this.handle = new DownloadJobHandle();
		}

		@Override
		public DownloadJobHandle getHandle() {
			return handle;
		}

		@Override
		public DownloadJobProgress getProgress() {
			return progress;
		}

		@Override
		public String getOutputFilename() {
			return outputFilename;
		}

		@Override
		public String getUrl() {
			return url;
		}

	}

}
