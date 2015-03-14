package io.github.lumue.getdown.job;

import io.github.lumue.getdown.job.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.persistence.MapBasedObjectRepository;
import io.github.lumue.getdown.persistence.ObjectBuilder;

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
