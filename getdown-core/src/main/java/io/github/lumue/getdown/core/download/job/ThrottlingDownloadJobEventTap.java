package io.github.lumue.getdown.core.download.job;

import static reactor.bus.selector.Selectors.$;
import io.github.lumue.getdown.core.common.util.ContentFilterEventTap;
import reactor.bus.EventBus;


/**
 * Forwarded nur alle X millisekunden einen neuen Zustand zu einer {@link DownloadJob} instanz
 * 
 * @author lm
 *
 */
public class ThrottlingDownloadJobEventTap extends ContentFilterEventTap<DownloadJob> {

	public ThrottlingDownloadJobEventTap(EventBus eventbus, String forwardSelectorKey, long millisecondsBetweenEventsPerDownloadJob) {
		super(eventbus, forwardSelectorKey, new DownloadJobThrottle(millisecondsBetweenEventsPerDownloadJob));

	}

}
