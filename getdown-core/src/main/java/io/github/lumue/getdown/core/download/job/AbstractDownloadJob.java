package io.github.lumue.getdown.core.download.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.util.Observable;
import io.github.lumue.getdown.core.common.util.ObservableTemplate;
import io.github.lumue.getdown.core.common.util.Observer;
import io.github.lumue.getdown.core.download.downloader.youtubedl.YoutubedlDownloadJob;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Optional;
import java.util.UUID;

import static io.github.lumue.getdown.core.download.job.DownloadState.FINISHED;

/**
 * Base class for DownloadsS
 */
public abstract class AbstractDownloadJob implements  java.io.Serializable, DownloadJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDownloadJob.class);


	private Long index=System.currentTimeMillis();

	private final String targetPath;
	
	private final DownloadTask downloadTask;


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
	private final String handle;
	@JsonProperty("name")
	private String name;
	@JsonProperty("url")
	private final String url;
	@JsonProperty("host")
	private final String host;
	@JsonProperty("workPath")
	private String workPath;
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


	public String getHandle() {
		return handle;
	}

	@Override
	public String getTargetPath() {
		return targetPath;
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
	protected AbstractDownloadJob(
			@JsonProperty("url") String url,
			@JsonProperty("handle") String handle,
			@JsonProperty("state") DownloadJobState downloadJobState,
			@JsonProperty("downloadProgress") DownloadProgress downloadProgress,
			@JsonProperty("name") String name,
			@JsonProperty("host") String host,
			@JsonProperty("index") Long index,
			@JsonProperty("targetPath") String targetPath,
			@JsonProperty("downloadTask") DownloadTask downloadTask,
			@JsonProperty("workPath") String downloadPath) {
		super();
		this.url = url;
		this.handle = handle;
		this.downloadJobState = downloadJobState;
		this.name = name;
		this.host = host;
		this.targetPath = targetPath;
		this.downloadTask = downloadTask;
		this.contentLocation = contentLocation;
		this.downloadProgress = downloadProgress;
		this.index=index;
	}

	public AbstractDownloadJob(
			String name,
			String url,
			String host,
			String handle,
			Long index,
			String targetPath,
			DownloadTask downloadTask,
			String downloadPath) {
		super();
		this.name = name;
		this.host = host;
		this.url = url;
		this.handle = handle;
		this.index=index;
		this.targetPath=targetPath;
		this.downloadTask = downloadTask;
		this.workPath =downloadPath;
	}


	@Override
	public String toString() {
		return "AbstractDownloadJob{" +
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
		if(!getMessage().isPresent()||!message.equals(getMessage())) {
			observableTemplate.doObserved(() -> {
				AbstractDownloadJob.this.message = message;
			});
		}
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
	
	@Override
	public Observable removeObservers() {
		observableTemplate.removeObservers();
		return this;
	}






	public String getWorkPath() {
		return workPath;
	}

	public String getHost() {
		return this.host;
	}
	
	public DownloadTask getDownloadTask() {
		return downloadTask;
	}
	
	@Override
	public void postProcess() {
		getDownloadProgress().ifPresent(downloadProgress -> {
			if (FINISHED.equals(downloadProgress.getState())) {
				String movingmsg = "moving downloaded files for download " + getName() + " to " + getTargetPath();
				LOGGER.info(movingmsg);
				try {
					message(movingmsg);
					Path target = Paths.get(getTargetPath());
					if (!Files.exists(target))
						Files.createDirectory(target);
					Files.list(Paths.get(getWorkPath()))
							.filter(p -> !Files.isDirectory(p))
							.map(p -> new Path[]{p, target.resolve(p.getFileName())})
							.map(pa -> {
										pa[1] = createUniqueFilenam(pa[1]);
										return pa;
									}
							)
							.forEach(pa -> {
								try {
									Files.move(pa[0], pa[1]);
									Files.setPosixFilePermissions(pa[1], PosixFilePermissions.fromString("rw-rw-rw-"));
								} catch (IOException e) {
									String msg = "error moving files for download " + getName() + " to " + getTargetPath();
									error(new RuntimeException(msg + ":" + e.getClass().getSimpleName(), e));
									LOGGER.error(msg, e);
									throw new RuntimeException("msg", e);
								}
							});
					String movedmsg = "moved files for download " + getName() + " to " + getTargetPath();
					message(movedmsg);
					finish();
					LOGGER.info(movedmsg);
				} catch (IOException e) {
					String msg = "error moving files for download " + getName() + " to " + getTargetPath();
					error(new RuntimeException(msg + ":" + e.getClass().getSimpleName(), e));
					LOGGER.error(msg, e);
					throw new RuntimeException("msg", e);
				}
			}
		});
	}
	
	private Path createUniqueFilenam(final Path basepath) {
		Path uniquepath = basepath;
		for (int i = 0; Files.exists(uniquepath); i++)
			uniquepath = appendCountToFile(basepath, i);
		return uniquepath;
	}
	
	private Path appendCountToFile(Path path, int i) {
		final String pathString = path.toString();
		boolean isInfoJson = pathString.endsWith(".info.json");
		String pathWithoutExt = !isInfoJson ? FilenameUtils.removeExtension(pathString) : pathString.replace(".info.json", "");
		String ext = !isInfoJson ? FilenameUtils.getExtension(pathString) : "info.json";
		final String newPath = pathWithoutExt + "_" + Integer.toString(i) + "." + ext;
		return new File(newPath).toPath();
	}
	
	
	public abstract static class DownloadBuilder implements ObjectBuilder<DownloadJob> {
		protected String url;
		protected String host;
		protected String name;
		protected String handle= UUID.randomUUID().toString();
		protected Long index=System.currentTimeMillis();
		protected String targetPath;
		protected DownloadTask downloadTask;
		
		public DownloadBuilder(DownloadTask downloadTask) {
			this.downloadTask=downloadTask;
			this.name=downloadTask.getName();
			this.targetPath=downloadTask.getTargetLocation();
			this.url=downloadTask.getSourceUrl();
		}
		
		public DownloadBuilder withIndex(long index){
			this.index=index;
			return this;
		}

		protected String downloadPath;



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
		public DownloadBuilder withHandle(String keyValue) {
			this.handle=keyValue;
			return this;
		}

		@Override
		public boolean hasHandle() {
			return !StringUtils.isEmpty(handle);
		}

		public DownloadBuilder withDownloadPath(String downloadPath) {
			this.downloadPath=downloadPath;
			return this;
		}

		public DownloadBuilder withTargetPath(String targetPath) {
			this.targetPath=targetPath;
			return this;
		}
	}
}
