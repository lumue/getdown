package io.github.lumue.getdown.core.download.downloader.youtubedl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.download.job.*;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class NativeDownloadJob extends AbstractDownloadJob {
	
	
	private final ProgressionListener progressionListener = (message, p) -> getDownloadProgress().ifPresent(dp ->
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
		preparing();
		new YoutubedlValidateTaskJob(getDownloadTask()).run();
		prepared();
	}
	
	@Override
	public void executeDownload() {
		progress(new DownloadProgress());
		start();
		getDownloadProgress().ifPresent(dp -> {
			try {
				dp.start();
				message("downloading...");
				final DownloadFilesStep downloadFilesStep = new DownloadFilesStep(
						getDownloadTask().getSelectedFormats(),
						getWorkPath(),
						progressionListener
				);
				downloadFilesStep.run();
				
				final String ouputfilename = "MERGED." + getDownloadTask().getExt();
				
				if (getDownloadTask().getSelectedFormats().size() == 2) {
					message("merging...");
					final MergeFilesStep mergeFilesStep = new MergeFilesStep(
							getDownloadTask().getSelectedFormats(),
							getWorkPath(),
							ouputfilename,
							progressionListener
					);
					mergeFilesStep.run();
					
					getDownloadTask().getSelectedFormats().forEach(f -> {
						FileUtils.deleteQuietly(new File(getWorkPath() + File.separator + f.getFilename()));
					});
				}
				
				
				final String finalFilename = getWorkPath() + File.separator + getDownloadTask().getName() + "." + getDownloadTask().getExt();
				final File finalFile = new File(finalFilename);
				if (finalFile.exists())
					FileUtils.deleteQuietly(finalFile);
				FileUtils.moveFile(
						new File(getWorkPath() + File.separator + ouputfilename),
						finalFile
				);
				
				
				message("writing info.json");
				
				FileUtils.writeStringToFile(new File(getWorkPath() + File.separator + getDownloadTask().getName() + ".info.json"), getDownloadTask().getInfoJsonString(), Charset.forName("UTF-8"));
				
				dp.finish();
				
			} catch (IOException e) {
				error(e);
			}
		});
		
		
	}
	
	
	@Override
	public void cancel() {
	
	}
	
	@Override
	public boolean isPrepared() {
		return DownloadJobState.PREPARED.equals(getState());
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
