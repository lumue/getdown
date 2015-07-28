package io.github.lumue.getdown.core.download.job;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DownloadJobThrottleTest {

	private static final int MIN_DELAY_BETWEEN_EVENTS = 1000;

	@Test
	public void testFailForImmediateSecondEvent() {
		
		DownloadJobThrottle throttle=new DownloadJobThrottle(MIN_DELAY_BETWEEN_EVENTS);
		DownloadJob downloadJob=new MockDownloadJob();
		
		assertTrue(throttle.test(downloadJob));
		assertFalse(throttle.test(downloadJob));
	}

	@Test
	public void testSuccessForDelayedSecondEvent() throws InterruptedException {
		
		DownloadJobThrottle throttle=new DownloadJobThrottle(MIN_DELAY_BETWEEN_EVENTS);
		DownloadJob downloadJob=new MockDownloadJob();
		
		assertTrue(throttle.test(downloadJob));
		sleep(MIN_DELAY_BETWEEN_EVENTS+1);
		assertTrue(throttle.test(downloadJob));
	}

}
