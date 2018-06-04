package io.github.lumue.getdown.core.download.downloader;

import io.github.lumue.getdown.core.download.task.DownloadFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class DownloadFilesStep extends AbstractStep {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFilesStep.class);
	private final List<DownloadFormat> selectedFormats;
	private final String downloadPath;
	private final FileDownloader fileDownloader=new ApacheHttpFileDownloader();
	
	DownloadFilesStep(List<DownloadFormat> selectedFormats,
	                  String downloadPath) {
		
		this.selectedFormats = selectedFormats;
		this.downloadPath = downloadPath;
	}
	
	@Override
	protected void execute() {
		final long totalExpectedSize = selectedFormats.stream()
				.mapToLong(DownloadFormat::getExpectedSize)
				.sum();
		initProgression(totalExpectedSize);
		try  {
			selectedFormats.forEach(format -> fileDownloader.downloadFile(
					format.getUrl(),
					format.getHttpHeaders(),
					downloadPath + File.separator + format.getFilename(),
					(this::incrementProgression)));
		} catch (Exception exception) {
			DownloadFilesStep.LOGGER.error("error downloading", exception);
			throw new RuntimeException("error downloading", exception);
		}
	}
	
	
	
	
	
}
