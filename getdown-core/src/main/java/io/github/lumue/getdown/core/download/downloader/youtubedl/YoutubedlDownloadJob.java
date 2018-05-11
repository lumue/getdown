package io.github.lumue.getdown.core.download.downloader.youtubedl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.download.job.AbstractDownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadJob;

import io.github.lumue.getdown.core.download.job.DownloadProgress;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.download.YdlFileDownload;
import io.github.lumue.ydlwrapper.metadata.single_info_json.SingleInfoJsonMetadataAccessor;
import io.github.lumue.ydlwrapper.metadata.statusmessage.YdlStatusMessage;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.lumue.getdown.core.download.job.DownloadState.*;

/**
 * Delegates actual downloading to {@link YdlDownloadTask}
 */
public class YoutubedlDownloadJob extends AbstractDownloadJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YoutubedlDownloadJob.class);
	private transient AtomicReference<YdlDownloadTask> downloadTask = new AtomicReference<>(null);
	private boolean forceMp4OnYoutube = true;
	private String pathToYdl = "/usr/bin/youtube-dl";
	
	
	@JsonCreator
	private YoutubedlDownloadJob(
			@JsonProperty("url") String url,
			@JsonProperty("handle") String handle,
			@JsonProperty("state") DownloadJob.DownloadJobState downloadJobState,
			@JsonProperty("downloadProgress") DownloadProgress downloadProgress,
			@JsonProperty("name") String name,
			@JsonProperty("host") String host,
			@JsonProperty("index") Long index,
			@JsonProperty("targetPath") String targetPath,
			@JsonProperty("downloadTask") DownloadTask downloadTask,
			@JsonProperty("downloadPath") String downloadPath) {
		super(url, handle, downloadJobState, downloadProgress, name, host, index, targetPath, downloadTask, downloadPath);
	}
	
	private YoutubedlDownloadJob(String handle,
	                             String name,
	                             String url,
	                             String host,
	                             String downloadPath,
	                             Long index,
	                             String targetPath,
	                             DownloadTask downloadTask) {
		super(name, url, host, handle, index, targetPath, downloadTask, downloadPath);
	}
	
	@Override
	public void prepare() {
		if (DownloadJobState.PREPARING.equals(downloadJobState))
			return;
		preparing();
		try {
			getYdlTask().prepare();
			
		} catch (Exception e) {
			handleError(e);
		}
	}
	
	private YdlDownloadTask getYdlTask() {
		YdlDownloadTask result = downloadTask.get();
		if (result == null) {
			
			YdlDownloadTask.YdlDownloadTaskBuilder builder = YdlDownloadTask.builder()
					.setUrl(getUrl())
					.setOutputFolder(getWorkPath())
					.setWriteInfoJson(true)
					.setPathToYdl(pathToYdl)
					.onStdout(this::handleMessage)
					.onStateChanged(this::handleProgress)
					.onNewOutputFile(this::handleProgress)
					.onOutputFileChange(this::handleProgress)
					.onPrepared(this::handlePrepared);
			
			if (getUrl().contains("youtube.com")) {
				builder.setForceMp4(isForceMp4OnYoutube());
			}
			
			result = builder
					.build();
			if (!downloadTask.compareAndSet(null, result)) {
				return downloadTask.get();
			}
		}
		return result;
	}
	
	
	@Override
	public void executeDownload() {
		progress(new DownloadProgress());
		try {
			getYdlTask().executeAsync();
			
			getDownloadProgress().ifPresent(p -> {
				boolean finished = false;
				while (!finished) {
					if (p.getState().equals(FINISHED)
							|| p.getState().equals(CANCELLED)
							|| p.getState().equals(ERROR)) {
						finished = true;
					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							LOGGER.error("interrupted waiting for youtube-dl download");
							cancel();
						}
					}
				}
			});
			
		} catch (Throwable t) {
			handleError(t);
		}
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
	
	private void handlePrepared(YdlDownloadTask ydlDownloadTask, SingleInfoJsonMetadataAccessor singleInfoJsonMetadataAccessor) {
		singleInfoJsonMetadataAccessor.getTitle().ifPresent(this::updateName);
		prepared();
	}
	
	private void handleMessage(YdlDownloadTask ydlDownloadTask, YdlStatusMessage ydlStatusMessage) {
		message(ydlStatusMessage.getLine());
	}
	
	private void handleError(Throwable t) {
		LOGGER.error("error during youtube-dl execution ", t);
		error(t);
		getDownloadProgress().ifPresent(downloadProgress -> downloadProgress.error(t));
	}
	
	private void handleProgress(YdlDownloadTask ydlDownloadTask, YdlDownloadTask.YdlDownloadState ydlDownloadState) {
		getDownloadProgress().ifPresent(downloadProgress -> {
			if (downloadProgress.getState().equals(WAITING)) {
				if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.EXECUTING)) {
					start();
					downloadProgress.start();
				}
			} else if (downloadProgress.getState().equals(DOWNLOADING)) {
				if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.SUCCESS)) {
					downloadProgress.finish();
					finish();
				} else if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.ERROR)) {
					Error error = new Error("error executing youtube-dl");
					downloadProgress.error(error);
					error(error);
				}
			}
			progress(downloadProgress);
		});
	}
	
	private void handleProgress(YdlDownloadTask ydlDownloadTask, YdlFileDownload ydlFileDownload) {
		getDownloadProgress().ifPresent(downloadProgress -> {
			if (ydlFileDownload.getExpectedSize() != null)
				downloadProgress.setSize(ydlFileDownload.getExpectedSize());
			if (ydlFileDownload.getDownloadedSize() != null)
				downloadProgress.updateDownloadedSize(ydlFileDownload.getDownloadedSize());
			progress(downloadProgress);
		});
	}
	
	@Override
	public void cancel() {
		doObserved(() -> {
			getYdlTask().cancel();
			getDownloadProgress().ifPresent(downloadProgress -> {
				downloadProgress.cancel();
				progress(downloadProgress);
			});
			downloadJobState = DownloadJobState.CANCELLED;
			message = "download cancelled";
		});
	}
	
	
	@Override
	public boolean isPrepared() {
		
		return getYdlTask().isPrepared();
	}
	
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		downloadTask = new AtomicReference<>(null);
	}
	
	public static YoutubedlDownloadJobBuilder builder(DownloadTask downloadTask) {
		return new YoutubedlDownloadJobBuilder(downloadTask);
	}
	
	public boolean isForceMp4OnYoutube() {
		return forceMp4OnYoutube;
	}
	
	public void setForceMp4OnYoutube(boolean forceMp4OnYoutube) {
		this.forceMp4OnYoutube = forceMp4OnYoutube;
	}
	
	public static class YoutubedlDownloadJobBuilder
			extends DownloadBuilder {
		
		public YoutubedlDownloadJobBuilder(DownloadTask downloadTask) {
			super(downloadTask);
		}
		
		@Override
		public DownloadJob build() {
			return new YoutubedlDownloadJob(this.handle, this.name, this.url, this.host, this.downloadPath, this.index, this.targetPath, this.downloadTask);
		}
		
	}
}
