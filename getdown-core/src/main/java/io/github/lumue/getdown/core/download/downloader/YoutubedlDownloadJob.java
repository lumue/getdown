package io.github.lumue.getdown.core.download.downloader;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
					.onStateChanged(this::handleStateChange)
					.onNewOutputFile(this::handleOutputFileChange)
					.onOutputFileChange(this::handleOutputFileChange)
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
	
	private void handlePrepared(YdlDownloadTask ydlDownloadTask, SingleInfoJsonMetadataAccessor singleInfoJsonMetadataAccessor) {
		singleInfoJsonMetadataAccessor.getTitle().ifPresent(this::updateName);
		prepared();
	}
	
	private void handleMessage(YdlDownloadTask ydlDownloadTask, YdlStatusMessage ydlStatusMessage) {
        LOGGER.debug("handleMessage({}{})",ydlDownloadTask,ydlStatusMessage);
		message(ydlStatusMessage.getLine());
	}
	
	private void handleError(Throwable t) {
		LOGGER.error("error during youtube-dl execution ", t);
		error(t);
		getDownloadProgress().ifPresent(downloadProgress -> downloadProgress.error(t));
	}
	
	private void handleStateChange(YdlDownloadTask ydlDownloadTask, YdlDownloadTask.YdlDownloadState ydlDownloadState) {
		LOGGER.debug("handleStateChange({}{})",ydlDownloadTask,ydlDownloadState);
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
	
	private void handleOutputFileChange(YdlDownloadTask ydlDownloadTask, YdlFileDownload ydlFileDownload) {
		LOGGER.debug("handleOutputFileChange({}{})",ydlDownloadTask,ydlFileDownload);
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
