package io.github.lumue.getdown.core.common.persistence;

import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.Download.DownloadJobHandle;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;

import java.util.Map;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class MapBasedDownloadJobRepository extends MapBasedObjectRepository<ObjectBuilder<DownloadJob>, DownloadJobHandle, DownloadJob>
		implements
		DownloadJobRepository {

	public MapBasedDownloadJobRepository(Map<DownloadJobHandle, DownloadJob> jobMap) {
		super(jobMap);
	}

}
