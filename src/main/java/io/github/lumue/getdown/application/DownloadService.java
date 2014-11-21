package io.github.lumue.getdown.application;

import io.github.lumue.getdown.application.DownloadJob.DownloadJobBuilder;
import io.github.lumue.getdown.application.DownloadJob.DownloadJobHandle;

import java.net.URI;
import java.nio.file.FileSystems;

import org.springframework.beans.factory.annotation.Autowired;
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
	private DownloadJobRepository jobRepository;

	private String downloadPath = "/home/lm/getdown";

	@Autowired
	private DownloadJobRunner downloadJobRunner;

	

	public DownloadJob addDownload(final String url) {
		String filename = resolveFilename(url);
		DownloadJobBuilder jobBuilder = new DownloadJobBuilder().withUrl(url).withOutputFilename(filename);
		return jobRepository.create(jobBuilder);
	}

	public void startDownload(final DownloadJobHandle handle) {
		DownloadJob job = jobRepository.get(handle);
		downloadJobRunner.runJob(job);
	}
	
	public Iterable<DownloadJob> listDownloads(){
		return this.jobRepository.list();
	}

	private String resolveFilename(final String url) {
		String path = URI.create(url).getPath();
		return FileSystems.getDefault().getPath(downloadPath, path.substring(path.lastIndexOf('/') + 1)).toString();
	}

}
