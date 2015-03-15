package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.persistence.SingleJsonFileObjectRepository;
import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;

import java.io.File;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class SingleJsonFileDownloadJobRepository extends
		SingleJsonFileObjectRepository<ObjectBuilder<DownloadJob>, DownloadJobHandle, DownloadJob>
		implements
		DownloadJobRepository {

	public SingleJsonFileDownloadJobRepository(String filename) {
		super(filename + File.separator + "download-jobs.json");
	}

	@Override
	protected TypeReference<DownloadJob[]> newTypeReference() {
		return new TypeReference<DownloadJob[]>() {

			@Override
			public Type getType() {
				return HttpDownloadJob[].class;
			}

		};
	}

}
