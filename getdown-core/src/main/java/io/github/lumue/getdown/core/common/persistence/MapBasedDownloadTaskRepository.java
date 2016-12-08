package io.github.lumue.getdown.core.common.persistence;

import io.github.lumue.getdown.core.download.task.DownloadTask;

import io.github.lumue.getdown.core.download.task.DownloadTaskRepository;

import java.util.Map;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class MapBasedDownloadTaskRepository
		extends MapBasedObjectRepository<ObjectBuilder<DownloadTask>, String, DownloadTask>
		implements DownloadTaskRepository {

	public MapBasedDownloadTaskRepository(Map<String, DownloadTask> backingMap) {
		super(backingMap);
	}

}
