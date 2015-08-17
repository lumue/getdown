package io.github.lumue.getdown.app.springboot.web;

import java.io.Serializable;
import java.util.Optional;

import io.github.lumue.getdown.core.download.downloader.DownloadProgress;
import io.github.lumue.getdown.core.download.job.DownloadJob;

public interface DownloadJobView extends Serializable {
	
	public String getHandle();
	public String getName();
	public String getUrl();
	public String getState();
	public Long getSize();
	public Long getProgress();
	public String getMessage();
	
	public static DownloadJobView wrap(final DownloadJob download)
	{
		return new DownloadJobView() {

			private static final long serialVersionUID = 2033910253254112701L;

			@Override
			public String getName() {
				String outputFilename = download.getOutputFilename();
				return outputFilename.substring(outputFilename.lastIndexOf('/') + 1);
			}

			@Override
			public String getUrl() {
				return download.getUrl();
			}

			@Override
			public Long getSize() {

				Optional<DownloadProgress> downloadProgress = download.getDownloadProgress();
				if (!downloadProgress.isPresent())
					return 1L;

				return downloadProgress.get().getSize();

			}

			@Override
			public Long getProgress() {
				Optional<DownloadProgress> downloadProgress = download.getDownloadProgress();
				if (!downloadProgress.isPresent())
					return 1L;

				return downloadProgress.get().getDownloadedSize();
			}

			@Override
			public String getHandle() {
				return download.getHandle().toString();
			}

			@Override
			public String getState() {
				return download.getState().name();
			}

			@Override
			public String getMessage() {
				return download.getMessage().orElse("");
			}
			
		};
	}
}
