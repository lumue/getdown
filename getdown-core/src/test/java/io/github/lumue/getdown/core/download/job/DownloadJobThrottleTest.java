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
		
		assertTrue("first call should always return true",throttle.test(downloadJob));
		assertFalse("should fail for immediate second call",throttle.test(downloadJob));
	}

	@Test
	public void testSuccessForDelayedSecondEvent() throws InterruptedException {
		
		DownloadJobThrottle throttle=new DownloadJobThrottle(MIN_DELAY_BETWEEN_EVENTS);
		DownloadJob downloadJob=new MockDownloadJob();
		
		assertTrue("first call should always return true",throttle.test(downloadJob));
		sleep(MIN_DELAY_BETWEEN_EVENTS+1);
		assertTrue("should succeed for delayed second call",throttle.test(downloadJob));
	}
	
	@Test
	public void testSuccessForImmediateSecondEventAndJobStateChange() throws InterruptedException {
		
		DownloadJobThrottle throttle=new DownloadJobThrottle(MIN_DELAY_BETWEEN_EVENTS);
		MockDownloadJob downloadJob=new MockDownloadJob();
		
		downloadJob.start();
		assertTrue("first call should always return true",throttle.test(downloadJob));
		downloadJob.finish();
		assertTrue("should succeed because the jobs state has changed",throttle.test(downloadJob));
	}

}
