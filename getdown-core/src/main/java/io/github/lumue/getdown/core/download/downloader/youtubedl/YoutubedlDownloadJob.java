package io.github.lumue.getdown.core.download.downloader.youtubedl;

import io.github.lumue.getdown.core.download.downloader.internal.ContentDownloader;
import io.github.lumue.getdown.core.download.job.Download;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadProgress;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.download.YdlFileDownload;
import io.github.lumue.ydlwrapper.metadata.single_info_json.SingleInfoJsonMetadataAccessor;
import io.github.lumue.ydlwrapper.metadata.statusmessage.YdlStatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lm on 15.03.16.
 */
public class YoutubedlDownloadJob extends Download implements DownloadJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(YoutubedlDownloadJob.class);
	private transient YdlDownloadTask downloadTask;
	private Long index=System.currentTimeMillis();

	public YoutubedlDownloadJob(String name,
	                            String url,
	                            String outputFilename,
	                            String host,
	                            String downloadPath) {
		super(name, url, outputFilename, host);
		setDownloadPath(downloadPath);
		downloadTask = YdlDownloadTask.builder()
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
	}

	@Override
	public void prepare() {
		if(DownloadJobState.PREPARING.equals(downloadJobState))
			return;
		preparing();
		downloadTask.prepare();
	}



	@Override
	public void run() {
		downloadTask = YdlDownloadTask.builder()
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

		progress(new DownloadProgress());
		try {
			downloadTask.executeAsync();

				getDownloadProgress().ifPresent(p -> {
					boolean finished=false;
					while (!finished) {
						if (getState().equals(DownloadJobState.FINISHED)
								|| getState().equals(DownloadJobState.CANCELLED)
								|| getState().equals(DownloadJobState.ERROR)){
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
			if (getState().equals(DownloadJobState.WAITING) || getState().equals(DownloadJobState.PREPARED)) {
				if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.EXECUTING)) {
					download();
				}
			} else if (getState().equals(ContentDownloader.DownloadState.DOWNLOADING)) {
				if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.SUCCESS)) {
					finish();
				} else if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.ERROR)) {
					Error error = new Error("error executing youtube-dl");
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
		doObserved("download-update-state",()-> {
			downloadTask.cancel();
			downloadJobState=DownloadJobState.CANCELLED;
		});
	}

	@Override
	public Long getIndex() {
		return index;
	}

	public static YoutubedlDownloadJobBuilder builder() {
		return new YoutubedlDownloadJobBuilder();
	}

	public static class YoutubedlDownloadJobBuilder
			extends DownloadBuilder {

		@Override
		public DownloadJob build() {
			return new YoutubedlDownloadJob(this.name, this.url, this.outputFilename, this.host,this.downloadPath);
		}

	}
}
