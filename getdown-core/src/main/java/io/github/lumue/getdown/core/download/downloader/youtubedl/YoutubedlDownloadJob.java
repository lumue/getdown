package io.github.lumue.getdown.core.download.downloader.youtubedl;

import io.github.lumue.getdown.core.download.downloader.internal.ContentDownloader;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadProgress;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.download.YdlFileDownload;
import io.github.lumue.ydlwrapper.metadata.statusmessage.YdlStatusMessage;

import static io.github.lumue.getdown.core.download.job.DownloadJob.*;

/**
 * Created by lm on 15.03.16.
 */
public class YoutubedlDownloadJob extends AbstractDownloadJob implements DownloadJob {

	private transient YdlDownloadTask downloadTask;

	public YoutubedlDownloadJob(String url, String outputFilename, String host) {
		super(url, outputFilename, host);
	}

	@Override
	public void run() {
		downloadTask = YdlDownloadTask.builder()
				.setUrl(getUrl())
				.setOutputFolder(getDownloadPath())
				.setWriteInfoJson(true)
				.onStdout(this::publishMessage)
				.onStateChanged(this::publishProgress)
				.onNewOutputFile(this::publishProgress)
				.onOutputFileChange(this::publishProgress)
				.build();

		progress(new DownloadProgress());
		try {
			downloadTask.execute();
		}
		catch(Throwable t){
			publishError(t);
		}
	}

	private void publishMessage(YdlDownloadTask ydlDownloadTask, YdlStatusMessage ydlStatusMessage) {
		message(ydlStatusMessage.getLine());
	}

	private void publishError(Throwable t) {
		error(t);
		getDownloadProgress().ifPresent(downloadProgress -> downloadProgress.error(t));
	}

	private void publishProgress(YdlDownloadTask ydlDownloadTask, YdlDownloadTask.YdlDownloadState ydlDownloadState) {
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

	private void publishProgress(YdlDownloadTask ydlDownloadTask, YdlFileDownload ydlFileDownload) {
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

	public static class YoutubedlDownloadJobBuilder
			extends AbstractDownloadJobBuilder {

		@Override
		public DownloadJob build() {
			return new YoutubedlDownloadJob(this.url, this.outputFilename,this.host);
		}

	}
}
