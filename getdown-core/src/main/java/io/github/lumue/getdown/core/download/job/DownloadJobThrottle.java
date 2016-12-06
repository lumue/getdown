package io.github.lumue.getdown.core.download.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.github.lumue.getdown.core.download.job.DownloadJob.DownloadJobState;

/**
 * return true for a given {@link DownloadJob} only if
 * any of this is true:
 * 
 * - there was no prior test for this job
 * - the last test for this job happened more than X milliseconds (set by constructor) ago
 * - the job had a different state on last test
 * @author lm
 *
 */
class DownloadJobThrottle implements Predicate<DownloadJob> {

	/**
	 * used for remembering the last call for a job
	 * @author lm
	 *
	 */
	private static class TestSample{
		
		private final long time;
		
		private final DownloadJobState state;
		
		public TestSample(long time, DownloadJobState state) {
			super();
			this.time = time;
			this.state = state;
		}	
	}
	
	private final Map<String, TestSample> jobHandleTestSampleMap = new ConcurrentHashMap<String, TestSample>();
	
	
	private final long millisecondsBetweenEventsPerDownloadJob;
		
	
	DownloadJobThrottle(long millisecondsBetweenDownloadJobEvents) {
		this.millisecondsBetweenEventsPerDownloadJob=millisecondsBetweenDownloadJobEvents;
	}

	@Override
	public boolean test(DownloadJob downloadJob) {

		TestSample newSample = new TestSample(System.currentTimeMillis(),downloadJob.getState());
		TestSample lastSample = jobHandleTestSampleMap.get(downloadJob.getHandle());
		
		if (lastSample == null)
			return putValueAndReturnTrue(downloadJob.getHandle(), newSample);
		
		Long lastUpdate = lastSample.time;
		DownloadJobState lastJobState=lastSample.state;

		long nextPossibleUpdateAt = lastUpdate + millisecondsBetweenEventsPerDownloadJob;

		if (System.currentTimeMillis() > nextPossibleUpdateAt)
			return putValueAndReturnTrue(downloadJob.getHandle(),  newSample);

		if(!lastJobState.equals(downloadJob.getState()))
			return putValueAndReturnTrue(downloadJob.getHandle(),  newSample);
		
		return false;
	}

	private boolean putValueAndReturnTrue(String downloadJobHandle, TestSample testSample) {
		jobHandleTestSampleMap.put(downloadJobHandle, testSample);
		return true;
	}

}