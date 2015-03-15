package io.github.lumue.getdown.core.download.job;

import static reactor.event.selector.Selectors.$;
import io.github.lumue.getdown.core.common.util.ContentFilterEventTap;
import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobHandle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import reactor.core.Reactor;

/**
 * Forwarded nur alle X millisekunden einen neuen Zustand zu einer {@link DownloadJob} instanz
 * 
 * @author lm
 *
 */
public class ThrottelingDownloadJobEventTap extends ContentFilterEventTap<DownloadJob> {

	/**
	 * Akzeptiert nur alle X millisekunden einen neuen Zustand zu einem DownloadJob
	 * 
	 * @author lm
	 *
	 */
	private static class DownloadJobThrottle implements Predicate<DownloadJob> {

		private final long millisecondsBetweenEventsPerDownloadJob;
		
		private final Map<DownloadJobHandle, Long> jobHandleTimestampMap = new ConcurrentHashMap<DownloadJob.DownloadJobHandle, Long>();

		private DownloadJobThrottle(long millisecondsBetweenDownloadJobEvents) {
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

	public ThrottelingDownloadJobEventTap(Reactor reactor, String forwardSelectorKey, long millisecondsBetweenEventsPerDownloadJob) {
		super(reactor, forwardSelectorKey, $("downloads"), new DownloadJobThrottle(millisecondsBetweenEventsPerDownloadJob));

	}

}
