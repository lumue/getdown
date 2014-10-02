package io.github.lumue.getdown.application;

import io.github.lumue.getdown.application.DownloadJob.DownloadJobBuilder;
import io.github.lumue.getdown.application.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.downloader.AsyncContentDownloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * manage the execution of downloads
 * 
 * @author lm
 *
 */
@Service
public class DownloadService {

	@Autowired
	private ExecutorService executorService;

	@Autowired
	private DownloadJobRepository jobRepository;

	@Value("{getdown.downloadPath}")
	private String downloadPath;

	private AsyncContentDownloader downloader;

	@PostConstruct
	public void init() {
		downloader = AsyncContentDownloader.builder().withExecutor(executorService).build();
	}

	public DownloadJobHandle addDownload(String url) {
		String filename = resolveFilename(url);
		DownloadJobBuilder jobBuilder = new DownloadJobBuilder().withUrl(url).withOutputFilename(filename);
		return jobRepository.create(jobBuilder).getHandle();
	}

	public void startDownload(DownloadJobHandle handle) {
		DownloadJob job = jobRepository.get(handle);
		try {
			downloader.downloadContent(URI.create(job.getUrl()), new FileOutputStream(job.getOutputFilename()), job.getProgressListener());
		} catch (IOException e) {
			// can not happen with async download, check download for error
			// state
		}
		;
	}

	private String resolveFilename(String url) {
		String path = URI.create(url).getPath();
		return FileSystems.getDefault().getPath(downloadPath, path.substring(path.lastIndexOf('/') + 1)).toString();
	}

}
