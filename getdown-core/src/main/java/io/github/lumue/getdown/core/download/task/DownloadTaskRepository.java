package io.github.lumue.getdown.core.download.task;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.persistence.ObjectRepository;


public interface DownloadTaskRepository extends ObjectRepository<ObjectBuilder< DownloadTask>, String, DownloadTask> {



}
