package io.github.lumue.getdown.core.download.downloader.youtubedl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.download.job.*;
import io.github.lumue.getdown.core.download.task.DownloadTask;

public class NativeDownloadJob extends AbstractDownloadJob {
	
	
	private final ProgressionListener progressionListener = (message,p) -> getDownloadProgress().ifPresent(dp ->
	{
		dp.setSize(p.getMax());
		dp.updateDownloadedSize(p.getValue());
		progress(dp);
		message(message);
	});
	
	@JsonCreator
	private NativeDownloadJob(
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
	
	private NativeDownloadJob(String name, String url, String host, String handle, Long index, String targetPath, DownloadTask downloadTask, String downloadPath) {
		super(name, url, host, handle, index, targetPath, downloadTask, downloadPath);
	}
	
	@Override
	public void prepare() {
		prepared();
	}
	
	@Override
	public void executeDownload() {
		progress(new DownloadProgress());
		start();
		
		message("downloading...");
		final DownloadFilesStep downloadFilesStep = new DownloadFilesStep(
				getDownloadTask().getSelectedFormats(),
				getWorkPath(),
				progressionListener
		);
		downloadFilesStep.run();
		
		if(getDownloadTask().getSelectedFormats().size()==2) {
			message("merging...");
			final MergeFilesStep mergeFilesStep = new MergeFilesStep(
					getDownloadTask().getSelectedFormats(),
					getWorkPath(),
					progressionListener
			);
			mergeFilesStep.run();
		}
		finish();
	}
	
	@Override
	public void postProcess() {
	
	}
	
	@Override
	public void cancel() {
	
	}
	
	@Override
	public boolean isPrepared() {
		return true;
	}
	
	public static NativeDownloadJobBuilder builder(DownloadTask downloadTask) {
		return new NativeDownloadJobBuilder(downloadTask);
	}
	
	public static class NativeDownloadJobBuilder
			extends DownloadBuilder {
		
		public NativeDownloadJobBuilder(DownloadTask downloadTask) {
			super(downloadTask);
		}
		
		@Override
		public DownloadJob build() {
			return new NativeDownloadJob(this.name, this.url, this.host, this.handle, this.index, this.targetPath, this.downloadTask, this.downloadPath);
		}
		
	}
}
