package io.github.lumue.getdown.job;

import io.github.lumue.getdown.job.DownloadJob.DownloadJobBuilder;
import io.github.lumue.getdown.job.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.persistence.SingleJsonFileObjectRepository;

import java.io.File;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class SingleJsonFileDownloadJobRepository extends SingleJsonFileObjectRepository<DownloadJobBuilder, DownloadJobHandle, DownloadJob>
		implements
		DownloadJobRepository {

	public SingleJsonFileDownloadJobRepository(String filename) {
		super(filename + File.separator + "download-jobs.json");
	}

}
