package io.github.lumue.getdown.core.download.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.util.Observable;
import io.github.lumue.getdown.core.common.util.ObservableTemplate;
import io.github.lumue.getdown.core.common.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Base class for DownloadsS
 */
public abstract class Download implements  java.io.Serializable, DownloadJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(Download.class);

	@JsonProperty("index")
	private Long index=System.currentTimeMillis();


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
		Download other = (Download) obj;
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

	public Long getIndex() {
		return index;
	}

	@JsonProperty("state")
	protected DownloadJob.DownloadJobState downloadJobState = DownloadJob.DownloadJobState.WAITING;

	@JsonProperty("downloadProgress")
	private DownloadProgress downloadProgress = null;

	@JsonProperty("message")
	protected String message = null;

	@JsonProperty("error")
	private Throwable error = null;

	private ContentLocation contentLocation = null;

	@JsonProperty("handle")
	private final DownloadJobHandle handle;
	@JsonProperty("name")
	private String name;
	@JsonProperty("outputFilename")
	private final String outputFilename;
	@JsonProperty("url")
	private final String url;
	@JsonProperty("host")
	private final String host;
	@JsonProperty("downloadPath")
	private String downloadPath;
	@JsonIgnore
	private final ObservableTemplate observableTemplate=new ObservableTemplate(this);

	public DownloadJob.DownloadJobState getState() {
		return downloadJobState;
	}


	public Optional<DownloadProgress> getDownloadProgress() {
		return Optional.ofNullable(downloadProgress);
	}

	public Optional<String> getMessage() {
		return Optional.ofNullable(message);
	}

	public Optional<Throwable> getError() {
		return Optional.ofNullable(error);
	}


	public DownloadJobHandle getHandle() {
		return handle;
	}

	public String getOutputFilename() {
		return outputFilename;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public Optional<ContentLocation> getContentLocation() {
		return Optional.ofNullable(contentLocation);
	}

	@JsonCreator
	protected Download(
			@JsonProperty("url") String url,
			@JsonProperty("outputFilename") String outputFilename,
			@JsonProperty("handle") DownloadJobHandle handle,
			@JsonProperty("state") DownloadJob.DownloadJobState downloadJobState,
			@JsonProperty("downloadProgress") DownloadProgress downloadProgress,
			String name,
			String host,
			Long index) {
		super();
		this.outputFilename = outputFilename;
		this.url = url;
		this.handle = handle;
		this.downloadJobState = downloadJobState;
		this.name = name;
		this.host = host;
		this.contentLocation = contentLocation;
		this.downloadProgress = downloadProgress;
		this.index=index;
	}

	public Download(
			String name,
			String url,
			String outputFilename,
			String host,
			DownloadJobHandle handle,
			Long index) {
		super();
		this.name = name;
		this.host = host;
		this.outputFilename = outputFilename;
		this.url = url;
		this.handle = handle;
		this.index=index;
	}

	public Download(
			String name,
			String url,
			String outputFilename,
			String host, Long index) {
		this(name, url, outputFilename, host, new DownloadJobHandle(),index);
	}

	@Override
	public String toString() {
		return "Download{" +
				"index=" + index +
				", handle=" + handle +
				", name='" + name + '\'' +
				", url='" + url + '\'' +
				'}';
	}



	protected void progress(DownloadProgress downloadProgress) {
		observableTemplate.doObserved(() -> {
			this.downloadProgress = downloadProgress;
		});
	}


	protected void start() {
		observableTemplate.doObserved(() -> {
			downloadJobState = DownloadJob.DownloadJobState.RUNNING;
			message = "initializing...";
		});
	}

	public void preparing() {
		observableTemplate.doObserved(() -> {
			downloadJobState = DownloadJob.DownloadJobState.PREPARING;
			message = "preparing...";
		});
	}


	public void waiting() {
		observableTemplate.doObserved(() -> {
			downloadJobState = DownloadJobState.WAITING;
			message = "waiting...";
		});
	}


	protected void prepared() {
		observableTemplate.doObserved(() -> {
			downloadJobState = DownloadJob.DownloadJobState.PREPARED;
			message = "prepare finished";
		});
	}



	protected void message(String message) {
		observableTemplate.doObserved(() -> {
			downloadJobState = DownloadJob.DownloadJobState.RUNNING;
			Download.this.message = message;
		});
	}

	protected void doObserved(ObservableTemplate.ObservedStateChange observedStateChange){
		observableTemplate.doObserved(observedStateChange);
	}


	protected void updateName(String name) {
		doObserved(() -> {
			this.name = name;
		});
	}



	protected void download() {
		doObserved(() -> {
			ContentLocation contentLocation = this.getContentLocation().get();
			message = "downloading from " + contentLocation.getUrl() + " to " + contentLocation.getFilename();
		});
	}

	protected void finish() {
		doObserved(() -> {
			downloadJobState = DownloadJob.DownloadJobState.FINISHED;
		});
	}

	protected void error(Throwable e) {
		doObserved(() -> {
			downloadJobState = DownloadJob.DownloadJobState.ERROR;
			message = "error: " + e.getLocalizedMessage();
		});
	}

	@Override
	public Observable addObserver(Observer<?> observer) {
		observableTemplate.addObserver(observer);
		return this;
	}

	@Override
	public Observable removeObserver(Observer<?> observer) {
		observableTemplate.addObserver(observer);
		return this;
	}


	protected void setContentLocation(ContentLocation contentLocation) {
		this.contentLocation = contentLocation;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}




	public String getDownloadPath() {
		return downloadPath;
	}

	public String getHost() {
		return this.host;
	}



	public abstract static class DownloadBuilder implements ObjectBuilder<DownloadJob> {
		protected String outputFilename;
		protected String url;
		protected String host;
		protected String name;
		private String handle=null;
		protected Long index=System.currentTimeMillis();

		public DownloadBuilder withIndex(long index){
			this.index=index;
			return this;
		}

		protected String downloadPath;

		public DownloadBuilder withOutputFilename(String outputFilename) {
			this.outputFilename = outputFilename;
			return this;
		}

		public DownloadBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public DownloadBuilder withUrl(String url) {
			this.url = url;
			this.host=URI.create(url).getHost();
			return this;
		}

		@Override
		public DownloadBuilder withKey(String keyValue) {
			this.handle=keyValue;
			return this;
		}



		public ObjectBuilder<DownloadJob> withDownloadPath(String downloadPath) {
			this.downloadPath=downloadPath;
			return this;
		}
	}
}
