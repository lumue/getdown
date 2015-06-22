package io.github.lumue.getdown.core.download.job;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.downloader.DownloadProgress;
import io.github.lumue.getdown.core.download.job.DownloadJob.AbstractDownloadJob.DownloadJobState;
import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.core.download.resolver.ContentLocation;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolverRegistry;

public interface DownloadJob extends HasIdentity<DownloadJobHandle>,Serializable {

	@Override
	public DownloadJobHandle getHandle();

	Optional<Throwable> getError();

	Optional<String> getMessage();

	Optional<DownloadProgress> getDownloadProgress();

	public DownloadJobState getState();

	public String getOutputFilename();

	public String getUrl();

	public Optional<ContentLocation> getContentLocation();

	public void run(String downloadPath, ContentLocationResolverRegistry contentLocationResolverRegistry,
			DownloadJobProgressListener progressListener);

	@FunctionalInterface
	public interface DownloadJobProgressListener {
		public void onChange(DownloadJob downloadJob);
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
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			DownloadJobHandle other = (DownloadJobHandle) obj;
			if (key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!key.equals(other.key)) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return key;
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

	public abstract class AbstractDownloadJob implements DownloadJob, java.io.Serializable {

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((handle == null) ? 0 : handle.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			AbstractDownloadJob other = (AbstractDownloadJob) obj;
			if (handle == null) {
				if (other.handle != null) {
					return false;
				}
			} else if (!handle.equals(other.handle)) {
				return false;
			}
			return true;
		}

		/**
		 *
		 */
		private static final long serialVersionUID = -1836164199304618485L;

		public static enum DownloadJobState {
			WAITING, RUNNING, ERROR, FINISHED;
		}

		@JsonProperty("state")
		private DownloadJobState downloadJobState = DownloadJobState.WAITING;

		@JsonProperty("downloadProgress")
		private Optional<DownloadProgress> downloadProgress = Optional.empty();

		private Optional<String> message = Optional.empty();

		private Optional<Throwable> error = Optional.empty();

		@JsonProperty("contentLocation")
		private Optional<ContentLocation> contentLocation=Optional.empty();

		@JsonProperty("handle")
		private final DownloadJobHandle handle;
		@JsonProperty("outputFilename")
		private final String outputFilename;
		@JsonProperty("url")
		private final String url;

		@Override
		public DownloadJobState getState() {
			return downloadJobState;
		}


		@Override
		public Optional<DownloadProgress> getDownloadProgress() {
			return downloadProgress;
		}

		@Override
		public Optional<String> getMessage() {
			return message;
		}

		@Override
		public Optional<Throwable> getError() {
			return error;
		}


		@Override
		public DownloadJobHandle getHandle() {
			return handle;
		}

		@Override
		public String getOutputFilename() {
			return outputFilename;
		}

		@Override
		public String getUrl() {
			return url;
		}



		@Override
		public Optional<ContentLocation> getContentLocation() {
			return contentLocation;
		}

		@JsonCreator
		private AbstractDownloadJob(
				@JsonProperty("url") String url,
				@JsonProperty("outputFilename") String outputFilename,
				@JsonProperty("handle") DownloadJobHandle handle,
				@JsonProperty("state") DownloadJobState downloadJobState,
				@JsonProperty("contentLocation") ContentLocation contentLocation,
				@JsonProperty("downloadProgress") DownloadProgress downloadProgress) {
			super();
			this.outputFilename = outputFilename;
			this.url = url;
			this.handle = handle;
			this.downloadJobState=downloadJobState;
			this.contentLocation=Optional.of(contentLocation);
			this.downloadProgress=Optional.of(downloadProgress);
		}

		public AbstractDownloadJob(
				String url,
				String outputFilename) {
			super();
			this.outputFilename = outputFilename;
			this.url = url;
			this.handle = new DownloadJobHandle();
		}



		protected void progress(DownloadJobProgressListener downloadJobProgressListener,DownloadProgress downloadProgress) {
			this.downloadProgress = Optional.of(downloadProgress);
			if(downloadJobProgressListener!=null) {
				downloadJobProgressListener.onChange(this);
			}
		}


		protected void start(DownloadJobProgressListener downloadJobProgressListener) {
			downloadJobState = DownloadJobState.RUNNING;
			message = Optional.of("initializing...");
			if(downloadJobProgressListener!=null) {
				downloadJobProgressListener.onChange(this);
			}
		}

		protected void resolve(DownloadJobProgressListener downloadJobProgressListener) {
			message = Optional.of("resolving " + this.url);
			if(downloadJobProgressListener!=null) {
				downloadJobProgressListener.onChange(this);
			}
		}

		protected void download(DownloadJobProgressListener downloadJobProgressListener) {
			ContentLocation contentLocation = this.getContentLocation().get();
			message = Optional.of("downloading from " +contentLocation.getUrl()+" to "+contentLocation.getFilename());
			if(downloadJobProgressListener!=null) {
				downloadJobProgressListener.onChange(this);
			}
		}

		protected void finish(DownloadJobProgressListener downloadJobProgressListener) {
			downloadJobState = DownloadJobState.FINISHED;
			if(downloadJobProgressListener!=null) {
				downloadJobProgressListener.onChange(this);
			}
		}

		protected void error(DownloadJobProgressListener downloadJobProgressListener,Throwable e) {
			downloadJobState=DownloadJobState.ERROR;
			message=Optional.of("error: "+e.getLocalizedMessage());
			if(downloadJobProgressListener!=null) {
				downloadJobProgressListener.onChange(this);
			}
		}

		protected void setContentLocation(ContentLocation contentLocation) {
			this.contentLocation=Optional.of(contentLocation);
		}




	}

}
