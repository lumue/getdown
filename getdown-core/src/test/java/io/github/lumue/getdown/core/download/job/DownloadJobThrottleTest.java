package io.github.lumue.getdown.core.download.job;

import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DownloadJobThrottleTest {

	private static final int MIN_DELAY_BETWEEN_EVENTS = 1000;

	@Test
	public void testFailForImmediateSecondEvent() {
		
		DownloadJobThrottle throttle=new DownloadJobThrottle(MIN_DELAY_BETWEEN_EVENTS);
		DownloadJob downloadJob=new MockDownloadJob();


		assertTrue(throttle.test(downloadJob),"first call should always return true");
		assertFalse(throttle.test(downloadJob),"first call should always return true");
	}

	@Test
	public void testSuccessForDelayedSecondEvent() throws InterruptedException {
		
		DownloadJobThrottle throttle=new DownloadJobThrottle(MIN_DELAY_BETWEEN_EVENTS);
		DownloadJob downloadJob=new MockDownloadJob();
		
		assertTrue(throttle.test(downloadJob),"first call should always return true");
		sleep(MIN_DELAY_BETWEEN_EVENTS+1);
		assertTrue(throttle.test(downloadJob),"should succeed for delayed second call");
	}
	
	@Test
	public void testSuccessForImmediateSecondEventAndJobStateChange() throws InterruptedException {
		
		DownloadJobThrottle throttle=new DownloadJobThrottle(MIN_DELAY_BETWEEN_EVENTS);
		MockDownloadJob downloadJob=new MockDownloadJob();
		
		downloadJob.start();
		assertTrue(throttle.test(downloadJob));
		downloadJob.finish();
		assertTrue(throttle.test(downloadJob));
	}

}
