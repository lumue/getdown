package io.github.lumue.getdown.core.download.downloader.youtubedl;

import io.github.lumue.getdown.core.download.downloader.internal.ContentDownloader;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadProgress;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.download.YdlFileDownload;
import io.github.lumue.ydlwrapper.metadata.single_info_json.SingleInfoJsonMetadataAccessor;
import io.github.lumue.ydlwrapper.metadata.statusmessage.YdlStatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.github.lumue.getdown.core.download.job.DownloadJob.*;

/**
 * Created by lm on 15.03.16.
 */
public class YoutubedlDownloadJob extends AbstractDownloadJob implements DownloadJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(YoutubedlDownloadJob.class);
	private transient YdlDownloadTask downloadTask;

	public YoutubedlDownloadJob(String name,String url, String outputFilename, String host) {
		super(name, url, outputFilename, host);
	}

	@Override
	public void run() {
		downloadTask = YdlDownloadTask.builder()
				.setUrl(getUrl())
				.setOutputFolder(getDownloadPath())
				.setWriteInfoJson(true)
				.onStdout(this::handleMessage)
				.onStateChanged(this::handleProgress)
				.onNewOutputFile(this::handleProgress)
				.onOutputFileChange(this::handleProgress)
				.onPrepared(this::handlePrepared)
				.build();

		progress(new DownloadProgress());
		try {
			downloadTask.execute();
		}
		catch(Throwable t){
			handleError(t);
		}
	}

	private void handlePrepared(YdlDownloadTask ydlDownloadTask, SingleInfoJsonMetadataAccessor singleInfoJsonMetadataAccessor) {
		singleInfoJsonMetadataAccessor.getTitle().ifPresent(this::updateName);
	}

	private void handleMessage(YdlDownloadTask ydlDownloadTask, YdlStatusMessage ydlStatusMessage) {
		message(ydlStatusMessage.getLine());
	}

	private void handleError(Throwable t) {
		LOGGER.error("error during youtube-dl execution ",t);
		error(t);
		getDownloadProgress().ifPresent(downloadProgress -> downloadProgress.error(t));
	}

	private void handleProgress(YdlDownloadTask ydlDownloadTask, YdlDownloadTask.YdlDownloadState ydlDownloadState) {
		getDownloadProgress().ifPresent(downloadProgress ->{
			if(downloadProgress.getState().equals(ContentDownloader.DownloadState.WAITING ))
			{
				if(ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.EXECUTING)) {
					start();
					downloadProgress.start();
				}
			}else if(downloadProgress.getState().equals(ContentDownloader.DownloadState.DOWNLOADING)){
				if(ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.SUCCESS)) {
					downloadProgress.finish();
					finish();
				}
				else if(ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.ERROR)) {
					Error error = new Error("error executing youtube-dl");
					downloadProgress.error(error);
					error(error);
				}
			}
			progress(downloadProgress);
		});
	}

	private void handleProgress(YdlDownloadTask ydlDownloadTask, YdlFileDownload ydlFileDownload) {
		getDownloadProgress().ifPresent(downloadProgress ->{
			if(ydlFileDownload.getExpectedSize()!=null)
				downloadProgress.setSize(ydlFileDownload.getExpectedSize());
			if(ydlFileDownload.getDownloadedSize()!=null)
				downloadProgress.updateDownloadedSize(ydlFileDownload.getDownloadedSize());
			progress(downloadProgress);
		});
	}

	@Override
	public void cancel() {

	}

	public static YoutubedlDownloadJobBuilder builder() {
		return new YoutubedlDownloadJobBuilder();
	}

	public static class YoutubedlDownloadJobBuilder
			extends AbstractDownloadJobBuilder {

		@Override
		public DownloadJob build() {
			return new YoutubedlDownloadJob(this.name,this.url, this.outputFilename,this.host);
		}

	}
}
