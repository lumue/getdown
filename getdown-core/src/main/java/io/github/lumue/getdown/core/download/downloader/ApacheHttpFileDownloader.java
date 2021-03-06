package io.github.lumue.getdown.core.download.downloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Consumer;

public class ApacheHttpFileDownloader implements FileDownloader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApacheHttpFileDownloader.class);
	
	private static final int BUFFER_SIZE = 1024 * 16;
	
	public void downloadFile(String sourceUrl, Map<String,String> headers, String filename, Consumer<Long> byteCountConsumer) {
		
		
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().disableContentCompression().build()){
			
			HttpGet get = new HttpGet(sourceUrl);
			
			long resumeAt = 0;
			
			final File file = new File(filename);
			if (file.exists()) {
				resumeAt = Files.size(file.toPath());
			}
			
			headers.entrySet().stream()
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
						}
						else{
							append=true;
							byteCountConsumer.accept(resumeAt);
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
							LOGGER.debug(bytesRead + " read from " + sourceUrl);
							outputStream.write(buffer, 0, bytesRead);
							LOGGER.debug(bytesRead + " written to " + filename);
							byteCountConsumer.accept((long) bytesRead);
						}
					}
				} else {
					if(status==HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
						Files.deleteIfExists(new File(filename).toPath());
					throw new RuntimeException("Unexpected response status: " + status);
				}
			}
		}catch(	IOException e)
		{
			throw new RuntimeException(e);
		}
		
	}
	
}
