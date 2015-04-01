package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.persistence.FileObjectRepository;
import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;

import java.io.File;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class FilePersistentDownloadJobRepository extends
		FileObjectRepository<ObjectBuilder<DownloadJob>, DownloadJobHandle, DownloadJob>
		implements
		DownloadJobRepository {

	public FilePersistentDownloadJobRepository(String filename) {
		super(filename + File.separator + "download-jobs.bin");
	}

}
