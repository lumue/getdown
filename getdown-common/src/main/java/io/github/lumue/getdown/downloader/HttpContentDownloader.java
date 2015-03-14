package io.github.lumue.getdown.downloader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class HttpContentDownloader implements ContentDownloader {

	private static final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();

	private static final HttpRequestFactory REQUEST_FACTORY = HTTP_TRANSPORT.createRequestFactory();
	


	@Override
	public void downloadContent(URI url, OutputStream targetStream, DownloadProgressListener progressListener)
			throws IOException {

		final DownloadProgress progress = new DownloadProgress();

		try {
			HttpRequest request = REQUEST_FACTORY.buildGetRequest(new GenericUrl(url));
			HttpResponse response = request.execute();
			Long size = response.getHeaders().getContentLength();
			progress.setSize(size);
			progress.start();
			if (progressListener != null) {
				progressListener.onChange(progress);
			}
			InputStream inputStream = response.getContent();
			int count;
			byte[] buffer = new byte[1024 * 512];
			while ((count = inputStream.read(buffer)) > 0) {
				targetStream.write(buffer, 0, count);
				progress.increaseDownloadedSize(count);
				if (progressListener != null) {
					progressListener.onChange(progress);
				}
			}
		} catch (IOException e) {
			

				progress.error(e);
			
			if (progressListener != null) {
				progressListener.onChange(progress);
			}

			throw e;
		}

			progress.finish();

		if (progressListener != null) {
			progressListener.onChange(progress);
		}

	}


}

