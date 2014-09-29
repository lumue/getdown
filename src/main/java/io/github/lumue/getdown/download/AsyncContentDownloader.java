package io.github.lumue.getdown.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class AsyncContentDownloader implements ContentDownloader {

	private static final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();

	private static final HttpRequestFactory REQUEST_FACTORY = HTTP_TRANSPORT.createRequestFactory();
	
	private static final Executor EXECUTOR = Executors.newCachedThreadPool();

	private static class AsyncContentDownloadProgress implements DownloadProgress {

		private final URI url;

		AsyncContentDownloadProgress(URI url) {
			super();
			this.url = url;
		}

		private DownloadState state = DownloadState.PENDING;
		private Long size = null; // null = unknown
		private long downloadedSize = 0;
		private Throwable error;

		@Override
		public Long getSize() {
			return size;
		}

		@Override
		public long getDownloadedSize() {
			return downloadedSize;
		}

		@Override
		public URI getUrl() {
			return url;
		}

		@Override
		public DownloadState getState() {
			return state;
		}

		void start() {
			this.state = DownloadState.RUNNING;
		}

		void finish() {
			this.state = DownloadState.FINISHED;
		}



		void error(Throwable t) {
			this.error = t;
			this.state = DownloadState.ERROR;
		}

		@Override
		public Throwable getError() {
			return error;
		}

	}

	@Override
	public DownloadProgress downloadContent(URI url, OutputStream targetStream) {

		final AsyncContentDownloadProgress progress = new AsyncContentDownloadProgress(url);
		
		EXECUTOR.execute(new Runnable() {

			@Override
			public void run() {



				try {
					HttpRequest request = REQUEST_FACTORY.buildGetRequest(new GenericUrl(url));
					HttpResponse response = request.execute();
					Long size = response.getHeaders().getContentLength();
					progress.size = size;

					progress.start();
					InputStream inputStream = response.getContent();
					int count;
					byte[] buffer = new byte[8192];
					while ((count = inputStream.read(buffer)) > 0) {
						targetStream.write(buffer, 0, count);
						progress.downloadedSize += count;
					}
				} catch (IOException e) {
					progress.error(e);
					return;
				}

				progress.finish();

			}
		});

		return progress;
		
	}

}
