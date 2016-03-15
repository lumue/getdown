package io.github.lumue.getdown.core.download.youtubedl;

import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.download.YdlFileDownload;

/**
 * Created by lm on 15.03.16.
 */
public class YoutubedlDownloadJob extends DownloadJob.AbstractDownloadJob implements DownloadJob {

	private YdlDownloadTask downloadTask;

	public YoutubedlDownloadJob(String url, String outputFilename, String host) {
		super(url, outputFilename, host);
	}

	@Override
	public void run() {
		downloadTask = YdlDownloadTask.builder()
				.setUrl("https://www.youtube.com/watch?v=BiG6_1LS_AI")
				.setOutputFolder(getOutputFilename())
				.setWriteInfoJson(true)
				.onNewOutputFile((ydlDownloadTask,ydlFileDownload)-> publishProgress(ydlDownloadTask,ydlFileDownload))
				.build();
	}

	private void publishProgress(YdlDownloadTask ydlDownloadTask, YdlFileDownload ydlFileDownload) {
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
