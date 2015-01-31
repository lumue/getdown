package io.github.lumue.getdown.job;

import io.github.lumue.getdown.job.DownloadJob.DownloadJobBuilder;
import io.github.lumue.getdown.job.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.persistence.ObjectRepository;

public interface DownloadJobRepository extends ObjectRepository<DownloadJobBuilder, DownloadJobHandle, DownloadJob> {


}
