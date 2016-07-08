package io.github.lumue.getdown.core.common.persistence.jdkmap;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.Download.DownloadJobHandle;
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
public class FilePersistentDownloadJobRepository extends
		JdkSerializableRepository<ObjectBuilder<DownloadJob>, DownloadJobHandle, DownloadJob>
		implements
		DownloadJobRepository {

	public FilePersistentDownloadJobRepository(String filename) {
		super(filename + File.separator + "download-jobs.bin");
	}

	@Override
	protected Collection<DownloadJob> getValues() {
		return super.getValues()
				.stream()
				.sorted((o1, o2) -> o1.getIndex().compareTo(o2.getIndex()))
				.collect(Collectors.toList());
	}
}
