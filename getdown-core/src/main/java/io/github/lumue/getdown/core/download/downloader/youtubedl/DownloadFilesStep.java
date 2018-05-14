package io.github.lumue.getdown.core.download.downloader.youtubedl;

import io.github.lumue.getdown.core.download.job.Progression;
import io.github.lumue.getdown.core.download.job.ProgressionListener;
import io.github.lumue.getdown.core.download.task.DownloadFormat;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DownloadFilesStep implements Runnable {
	
	class RangeNotSatisfiableException extends RuntimeException{
	
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFilesStep.class);
	private static final int BUFFER_SIZE = 1024 * 16;
	private final List<DownloadFormat> selectedFormats;
	private final String downloadPath;
	private final ProgressionListener progressionListener;
	private Progression progression;
	
	public DownloadFilesStep(List<DownloadFormat> selectedFormats,
	                         String downloadPath,
	                         ProgressionListener progressionListener) {
		
		this.selectedFormats = selectedFormats;
		this.downloadPath = downloadPath;
		this.progressionListener = progressionListener;
	}
	
	@Override
	public void run() {
		final long totalExpectedSize = selectedFormats.stream()
				.mapToLong(DownloadFormat::getExpectedSize)
				.sum();
		progression = new Progression(0, totalExpectedSize);
		progressionListener.onProgress("starting downloads", progression);
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().disableContentCompression().build()) {
			selectedFormats.forEach(format -> downloadFile(httpClient, format));
		} catch (Exception exception) {
			LOGGER.error("error downloading", exception);
			throw new RuntimeException("error downloading", exception);
		}
	}
	
	private void downloadFile(CloseableHttpClient httpClient, DownloadFormat format) {
		HttpGet get = new HttpGet(format.getUrl());
		String filename = downloadPath + File.separator + format.getFilename();
		
		try {
			long resumeAt = 0;
			
			final File file = new File(filename);
			if (file.exists()) {
				resumeAt = Files.size(file.toPath());
			}
			
			format.getHttpHeaders().entrySet().stream()
					.map(h -> new BasicHeader(h.getKey(), h.getValue()))
					.forEach(get::addHeader);
			
			if(resumeAt>0L) {
				get.addHeader("Range", "bytes=" + resumeAt + "-");
			}
			
			try (CloseableHttpResponse response = httpClient.execute(get)) {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					
					boolean append = false;
					
					if (resumeAt > 0L) {
						if (status != HttpStatus.SC_PARTIAL_CONTENT) {
							Files.deleteIfExists(new File(filename).toPath());
							resumeAt = 0L;
						}
						else{
							append=true;
							progressionListener.onProgress("resuming ",progression.incrementProgress(resumeAt));
						}
					}
				
					
					// opens an output stream to save into file
					try (
							FileOutputStream outputStream = new FileOutputStream(filename,append);
							InputStream inputStream = entity.getContent()
					) {
						int bytesRead;
						byte[] buffer = new byte[BUFFER_SIZE];
						while ((bytesRead = inputStream.read(buffer)) != -1) {
							LOGGER.debug(bytesRead + " read from " + format.getUrl());
							outputStream.write(buffer, 0, bytesRead);
							LOGGER.debug(bytesRead + " written to " + filename);
							progressionListener.onProgress("downloading " + format.getFilename(), progression.incrementProgress(bytesRead));
						}
					}
				} else {
					throw new RuntimeException("Unexpected response status: " + status);
				}
			}
		}catch(	IOException e)
		{
			throw new RuntimeException(e);
		}
		
	}
}
