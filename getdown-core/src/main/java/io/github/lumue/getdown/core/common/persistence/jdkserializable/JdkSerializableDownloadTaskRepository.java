package io.github.lumue.getdown.core.common.persistence.jdkserializable;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.job.DownloadJob;

import io.github.lumue.getdown.core.download.task.DownloadTask;
import io.github.lumue.getdown.core.download.task.DownloadTaskRepository;

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
public class JdkSerializableDownloadTaskRepository extends
		JdkSerializableRepository<ObjectBuilder<DownloadTask>, String, DownloadTask>
		implements
		DownloadTaskRepository {

	public JdkSerializableDownloadTaskRepository(String filename) {
		super(filename + File.separator + "download-tasks.bin");
	}

	@Override
	protected Collection<DownloadTask> getValues() {
		return super.getValues()
				.stream()
				.sorted(Comparator.comparing(DownloadTask::getPriority))
				.collect(Collectors.toList());
	}
}
