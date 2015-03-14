package io.github.lumue.getdown.job;

import io.github.lumue.getdown.job.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.persistence.ObjectBuilder;
import io.github.lumue.getdown.persistence.SingleJsonFileObjectRepository;

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
