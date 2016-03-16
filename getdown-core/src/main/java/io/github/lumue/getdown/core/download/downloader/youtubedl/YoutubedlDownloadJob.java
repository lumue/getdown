package io.github.lumue.getdown.core.download.downloader.youtubedl;

import io.github.lumue.getdown.core.common.util.Observable;
import io.github.lumue.getdown.core.common.util.Observer;
import io.github.lumue.getdown.core.download.job.DownloadProgress;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.ContentLocation;
import io.github.lumue.getdown.core.download.job.ContentLocationResolverRegistry;

import java.util.Optional;

/**
 * Created by lm on 15.03.16.
 */
public class YoutubedlDownloadJob implements DownloadJob{
	@Override
	public DownloadJobHandle getHandle() {
		return null;
	}

	@Override
	public Optional<Throwable> getError() {
		return null;
	}

	@Override
	public Optional<String> getMessage() {
		return null;
	}

	@Override
	public Optional<DownloadProgress> getDownloadProgress() {
		return null;
	}

	@Override
	public AbstractDownloadJob.DownloadJobState getState() {
		return null;
	}

	@Override
	public String getOutputFilename() {
		return null;
	}

	@Override
	public String getUrl() {
		return null;
	}

	@Override
	public String getHost() {
		return null;
	}

	@Override
	public Optional<ContentLocation> getContentLocation() {
		return null;
	}

	@Override
	public void setDownloadPath(String downloadPath) {

	}

	@Override
	public void setContentLocationResolverRegistry(ContentLocationResolverRegistry contentLocationResolverRegistry) {

	}

	@Override
	public void run() {

	}

	@Override
	public void cancel() {

	}

	@Override
	public Observable addObserver(Observer<?> observer) {
		return null;
	}

	@Override
	public Observable removeObserver(Observer<?> observer) {
		return null;
	}
}
