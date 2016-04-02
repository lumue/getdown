package io.github.lumue.getdown.core.common.persistence.chroniclemap;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;

import java.io.File;
import java.io.IOException;

/**
 * Persist {@link DownloadJob}s in a persistent {@link net.openhft.chronicle.map.ChronicleMap}
 *
 * Created by lm on 02.04.16.
 */
public class ChronicleMapDownloadJobRepository extends
		ChronicleMapRepository<ObjectBuilder<DownloadJob>, DownloadJob.DownloadJobHandle, DownloadJob>
		implements
		DownloadJobRepository {

	private static final String AVERAGE_JOB_HANDLE_KEY = "49384c96-f8c5-11e5-9ce9-5e5517507c66";
	private static final DownloadJob.DownloadJobHandle AVERAGE_KEY = new DownloadJob.DownloadJobHandle(AVERAGE_JOB_HANDLE_KEY);
	private static final Class<DownloadJob> VALUE_TYPE = DownloadJob.class;
	private static final Class<DownloadJob.DownloadJobHandle> KEY_TYPE = DownloadJob.DownloadJobHandle.class;
	private static final String FILENAME = "download-jobs.chronicle-map-store";

	private static final DownloadJob AVERAGE_VALUE= new DownloadJob.AbstractDownloadJob("joe-job","http://average.joe/download-me.mp4","download-me.mp4","average",AVERAGE_KEY) {
		@Override
		public void run() {}
		@Override
		public void cancel() {}
	};

	public ChronicleMapDownloadJobRepository(String repositoryPath) throws IOException {
		super(repositoryPath + File.separator + FILENAME,
				KEY_TYPE,
				VALUE_TYPE,
				AVERAGE_KEY,
				AVERAGE_VALUE);
	}
}
