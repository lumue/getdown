package io.github.lumue.getdown.web;

import io.github.lumue.getdown.application.DownloadJob;

public interface DownloadViewItem {
	public String getName();
	public String getURL();
	public Long getSize();
	public Long getProgress();
	
	public static DownloadViewItem wrap( final DownloadJob download)
	{
		return new DownloadViewItem(){

			@Override
			public String getName() {
				return download.getOutputFilename();
			}

			@Override
			public String getURL() {
				return download.getUrl();
			}

			@Override
			public Long getSize() {
				return download.getProgressListener().getSize();
			}

			@Override
			public Long getProgress() {
				return download.getProgressListener().getDownloadedSize();
			}
			
		};
	}
}
