package io.github.lumue.getdown.core.common.persistence.redis;

import io.github.lumue.getdown.core.download.job.DownloadJob;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by lm on 21.08.16.
 */
interface SpringDataDownloadJobRepo  extends CrudRepository<DownloadJob, String> {

}