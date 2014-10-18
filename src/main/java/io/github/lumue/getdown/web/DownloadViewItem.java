package io.github.lumue.getdown.web;

import java.io.Serializable;

import io.github.lumue.getdown.application.DownloadJob;

public interface DownloadViewItem extends Serializable{
	
	public String getHandle();
	public String getName();
	public String getURL();
	public Long getSize();
	public Long getProgress();
	
	public static DownloadViewItem wrap( final DownloadJob download)
	{
		return new DownloadViewItem(){

			/**
			 * 
			 */
			private static final long serialVersionUID = -1845543382826294810L;

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

			@Override
			public String getHandle() {
				return download.getHandle().toString();
			}
			
		};
	}
}
