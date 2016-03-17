package io.github.lumue.getdown.core.download.job;

import java.io.Serializable;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.util.Observable;
import io.github.lumue.getdown.core.common.util.ObservableTemplate;
import io.github.lumue.getdown.core.download.job.DownloadJob.AbstractDownloadJob.DownloadJobState;
import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;

public interface DownloadJob extends HasIdentity<DownloadJobHandle>,Serializable,Observable,Runnable {

	@Override
	public DownloadJobHandle getHandle();

	Optional<Throwable> getError();

	Optional<String> getMessage();

	Optional<DownloadProgress> getDownloadProgress();

	public DownloadJobState getState();

	public String getOutputFilename();

	public String getUrl();

	public String getHost();

	public Optional<ContentLocation> getContentLocation();
	
	public void setDownloadPath(String downloadPath);
	
	public void setContentLocationResolverRegistry(ContentLocationResolverRegistry contentLocationResolverRegistry);

	public void run();



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
		protected String outputFilename;
		protected String url;
		protected String host;

		public AbstractDownloadJobBuilder withOutputFilename(String outputFilename) {
			this.outputFilename = outputFilename;
			return this;
		}

		public AbstractDownloadJobBuilder withUrl(String url) {
			this.url = url;
			this.host=URI.create(url).getHost();
			return this;
		}
	}

	public abstract class AbstractDownloadJob extends ObservableTemplate implements DownloadJob, java.io.Serializable {

		private static final Logger LOGGER=LoggerFactory.getLogger(AbstractDownloadJob.class);


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
			WAITING, RUNNING, ERROR, FINISHED, CANCELLED;
		}

		@JsonProperty("state")
		protected DownloadJobState downloadJobState = DownloadJobState.WAITING;

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
		@JsonProperty("host")
		private final String host;
		@JsonProperty("downloadPath")
		private String downloadPath;

		private ContentLocationResolverRegistry contentLocationResolverRegistry;

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
				@JsonProperty("downloadProgress") DownloadProgress downloadProgress, String host) {
			super();
			this.outputFilename = outputFilename;
			this.url = url;
			this.handle = handle;
			this.downloadJobState=downloadJobState;
			this.host = host;
			this.contentLocation=Optional.of(contentLocation);
			this.downloadProgress=Optional.of(downloadProgress);
		}

		public AbstractDownloadJob(
				String url,
				String outputFilename,
				String host) {
			super();
			this.host=host;
			this.outputFilename = outputFilename;
			this.url = url;
			this.handle = new DownloadJobHandle();
		}



		protected void progress(DownloadProgress downloadProgress) {
			doObserved(() -> {
			this.downloadProgress = Optional.of(downloadProgress);
			});
		}


		protected void start() {
			doObserved(() -> {
				downloadJobState = DownloadJobState.RUNNING;
				message = Optional.of("initializing...");
			});
		}

		protected void message(String message) {
			doObserved(() -> {
				downloadJobState = DownloadJobState.RUNNING;
				AbstractDownloadJob.this.message = Optional.of(message);
			});
		}

		protected void resolve() {
			doObserved(() -> {
				message = Optional.of("resolving " + this.url);
				URI startUrl = URI.create(getUrl());
				LOGGER.debug("creating ContentLocation for " + startUrl.toString());

				ContentLocationResolver locationResolver = getContentLocationResolverRegistry()
						.lookup(host);
				ContentLocation contentLocation = null;

				if (locationResolver != null) {
					try {
						contentLocation = locationResolver.resolve(this);
					} catch (Exception e) {
						this.error(e);
						LOGGER.error(e.getLocalizedMessage(),e);
					}
				} else {
					LOGGER.warn(
							"no suitable resolver for host. creating generic ContentLocation from url");
					contentLocation = new ContentLocation(getUrl(),
							getOutputFilename());
				}

				LOGGER.debug(
						"created " + contentLocation + " for " + startUrl.toString());

				if(contentLocation!=null)
					this.setContentLocation(contentLocation);
			});
		}

		protected void download() {
			doObserved(() -> {
				ContentLocation contentLocation = this.getContentLocation().get();
			message = Optional.of("downloading from " +contentLocation.getUrl()+" to "+contentLocation.getFilename());
			});
		}

		protected void finish() {
			doObserved(() -> {
				downloadJobState = DownloadJobState.FINISHED;
			});
		}

		protected void error(Throwable e) {
			doObserved(() -> {
				downloadJobState=DownloadJobState.ERROR;
			message=Optional.of("error: "+e.getLocalizedMessage());
			});
		}

		protected void setContentLocation(ContentLocation contentLocation) {
			this.contentLocation=Optional.of(contentLocation);
		}

		public void setDownloadPath(String downloadPath){
			this.downloadPath=downloadPath;
		}

		@Override
		public void setContentLocationResolverRegistry(
				ContentLocationResolverRegistry contentLocationResolverRegistry) {
			this.contentLocationResolverRegistry=contentLocationResolverRegistry;
		}

		public ContentLocationResolverRegistry getContentLocationResolverRegistry() {
			return contentLocationResolverRegistry;
		}

		public String getDownloadPath() {
			return downloadPath;
		}

		@Override
		public String getHost() {
			return this.host;
		}
	}



	public void cancel();

	
	

	

}
