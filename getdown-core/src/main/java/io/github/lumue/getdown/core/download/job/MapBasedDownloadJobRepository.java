package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.common.persistence.MapBasedObjectRepository;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;

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
