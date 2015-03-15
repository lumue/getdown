package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.persistence.ObjectRepository;
import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;

public interface DownloadJobRepository extends ObjectRepository<ObjectBuilder<DownloadJob>, DownloadJobHandle, DownloadJob> {


}
