package io.github.lumue.getdown.app.springboot.web;

import io.github.lumue.getdown.core.download.job.DownloadJob;

import java.io.Serializable;

public interface DownloadJobView extends Serializable {
	
	public String getHandle();
	public String getName();
	public String getUrl();
	public String getState();
	public Long getSize();
	public Long getProgress();
	
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
				// TODO implement
				return 1L;
			}

			@Override
			public Long getProgress() {
				// TODO implement
				return 0L;
			}

			@Override
			public String getHandle() {
				return download.getHandle().toString();
			}

			@Override
			public String getState() {
				return download.getProgress().getState().name();
			}
			
		};
	}
}
