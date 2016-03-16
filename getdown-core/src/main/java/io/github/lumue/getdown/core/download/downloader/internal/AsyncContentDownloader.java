package io.github.lumue.getdown.core.download.downloader.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Utility class to execute asynchronous downloads
 * 
 * @author lm
 *
 */
public class AsyncContentDownloader implements ContentDownloader {

	

	private final ContentDownloader delegate;
	private final Executor executor;

	private AsyncContentDownloader(ContentDownloader delegate, Executor executor) {
		super();
		this.executor = executor;
		this.delegate = delegate;
	}

	@Override
	public void downloadContent(URI url, final OutputStream targetStream, final DownloadProgressListener progress) {

		this.executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					AsyncContentDownloader.this.delegate.downloadContent(url, targetStream,
							progress);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		
	}

	/**
	 * delegate actual downloading to given downloader
	 * 
	 * @param downloader
	 * @return
	 */
	public static class AsyncContentDownloaderBuilder {

		private Executor executor;
		private ContentDownloader delegate;

		public AsyncContentDownloaderBuilder withExecutor(Executor executor) {
			this.executor = executor;
			return this;
		}

		public AsyncContentDownloaderBuilder withDelegate(ContentDownloader delegate) {
			this.delegate = delegate;
			return this;
		}

		public AsyncContentDownloader build() {
			return new AsyncContentDownloader(delegate != null ? delegate : new HttpContentDownloader(), executor != null ? executor
					: Executors.newSingleThreadExecutor());
		}
	}

	public static AsyncContentDownloaderBuilder builder()
	{
		return new AsyncContentDownloaderBuilder();
	}
}
