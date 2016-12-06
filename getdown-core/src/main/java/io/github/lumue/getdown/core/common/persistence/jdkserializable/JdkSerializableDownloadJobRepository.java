package io.github.lumue.getdown.core.common.persistence.jdkserializable;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.job.DownloadJob;

import io.github.lumue.getdown.core.download.job.DownloadJobRepository;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class JdkSerializableDownloadJobRepository extends
		JdkSerializableRepository<ObjectBuilder<DownloadJob>, String, DownloadJob>
		implements
		DownloadJobRepository {

	public JdkSerializableDownloadJobRepository(String filename) {
		super(filename + File.separator + "download-jobs.bin");
	}

	@Override
	protected Collection<DownloadJob> getValues() {
		return super.getValues()
				.stream()
				.sorted(Comparator.comparing(DownloadJob::getIndex))
				.collect(Collectors.toList());
	}
}
