package io.github.lumue.getdown.core.download.downloader.youtubedl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.download.downloader.internal.ContentDownloader;
import io.github.lumue.getdown.core.download.job.ContentLocation;
import io.github.lumue.getdown.core.download.job.Download;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadProgress;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.download.YdlFileDownload;
import io.github.lumue.ydlwrapper.metadata.single_info_json.SingleInfoJsonMetadataAccessor;
import io.github.lumue.ydlwrapper.metadata.statusmessage.YdlStatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Delegates actual downloading to {@link YdlDownloadTask}
 */
public class YoutubedlDownloadJob extends Download {

	private static final Logger LOGGER = LoggerFactory.getLogger(YoutubedlDownloadJob.class);
	private transient AtomicReference<YdlDownloadTask> downloadTask=new AtomicReference<>(null);


	@JsonCreator
	private YoutubedlDownloadJob(
			@JsonProperty("url") String url,
			@JsonProperty("outputFilename") String outputFilename,
			@JsonProperty("handle") DownloadJobHandle handle,
			@JsonProperty("state") DownloadJob.DownloadJobState downloadJobState,
			@JsonProperty("downloadProgress") DownloadProgress downloadProgress,
			@JsonProperty("name") String name,
			@JsonProperty("host") String host) {
		super(url,outputFilename,handle,downloadJobState,downloadProgress,name,host);
	}

	private YoutubedlDownloadJob(String name,
	                             String url,
	                             String host,
	                             String downloadPath,
	                             Long index) {
		super(name, url, "", host,index);
		setDownloadPath(downloadPath);
	}

	@Override
	public void prepare() {
		if(DownloadJobState.PREPARING.equals(downloadJobState))
			return;
		preparing();
		getDownloadTask().prepare();
	}

	private YdlDownloadTask getDownloadTask() {
		YdlDownloadTask result = downloadTask.get();
		if (result == null) {
			result = YdlDownloadTask.builder()
					.setUrl(getUrl())
					.setOutputFolder(getDownloadPath())
					.setWriteInfoJson(true)
					.setPathToYdl("/usr/bin/youtube-dl")
					.onStdout(this::handleMessage)
					.onStateChanged(this::handleProgress)
					.onNewOutputFile(this::handleProgress)
					.onOutputFileChange(this::handleProgress)
					.onPrepared(this::handlePrepared)
					.build();
			if (!downloadTask.compareAndSet(null, result)) {
				return downloadTask.get();
			}
		}
		return result;
	}


	@Override
	public void run() {

		progress(new DownloadProgress());
		try {
			getDownloadTask().executeAsync();

				getDownloadProgress().ifPresent(p -> {
					boolean finished=false;
					while (!finished) {
						if (p.getState().equals(ContentDownloader.DownloadState.FINISHED)
								|| p.getState().equals(ContentDownloader.DownloadState.CANCELLED)
								|| p.getState().equals(ContentDownloader.DownloadState.ERROR)){
							finished=true;
						}
						else{
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
		message(ydlStatusMessage.getLine());
	}

	private void handleError(Throwable t) {
		LOGGER.error("error during youtube-dl execution ", t);
		error(t);
		getDownloadProgress().ifPresent(downloadProgress -> downloadProgress.error(t));
	}

	private void handleProgress(YdlDownloadTask ydlDownloadTask, YdlDownloadTask.YdlDownloadState ydlDownloadState) {
		getDownloadProgress().ifPresent(downloadProgress -> {
			if (downloadProgress.getState().equals(ContentDownloader.DownloadState.WAITING)) {
				if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.EXECUTING)) {
					start();
					downloadProgress.start();
				}
			} else if (downloadProgress.getState().equals(ContentDownloader.DownloadState.DOWNLOADING)) {
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
		doObserved(()-> {
			getDownloadTask().cancel();
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

		return getDownloadTask().isPrepared();
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		downloadTask=new AtomicReference<>(null);
	}

	public static YoutubedlDownloadJobBuilder builder() {
		return new YoutubedlDownloadJobBuilder();
	}

	public static class YoutubedlDownloadJobBuilder
			extends DownloadBuilder {

		@Override
		public DownloadJob build() {
			return new YoutubedlDownloadJob(this.name, this.url, this.host,this.downloadPath,this.index);
		}

	}
}
