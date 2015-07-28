package io.github.lumue.getdown.core.download.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;

/**
 * Akzeptiert nur alle X millisekunden einen DownloadJob
 * 
 * @author lm
 *
 */
class DownloadJobThrottle implements Predicate<DownloadJob> {

	private final long millisecondsBetweenEventsPerDownloadJob;
	
	private final Map<DownloadJobHandle, Long> jobHandleTimestampMap = new ConcurrentHashMap<DownloadJob.DownloadJobHandle, Long>();

	DownloadJobThrottle(long millisecondsBetweenDownloadJobEvents) {
		this.millisecondsBetweenEventsPerDownloadJob=millisecondsBetweenDownloadJobEvents;
	}

	@Override
	public boolean test(DownloadJob t) {

		
		Long lastUpdate = jobHandleTimestampMap.get(t.getHandle());
		long now = System.currentTimeMillis();

		if (lastUpdate == null)
			return putValueAndReturnTrue(t.getHandle(), now);

		long nextPossibleUpdateAt = lastUpdate + millisecondsBetweenEventsPerDownloadJob;

		if (now > nextPossibleUpdateAt)
			return putValueAndReturnTrue(t.getHandle(), now);

		return false;
	}

	private boolean putValueAndReturnTrue(DownloadJobHandle downloadJobHandle, long now) {
		jobHandleTimestampMap.put(downloadJobHandle, now);
		return true;
	}

}