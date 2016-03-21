package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.download.job.DownloadJob.AbstractDownloadJob;
/**
 * A DownloadJob for testing
 * @author lm
 *
 */
@SuppressWarnings("serial")
public class MockDownloadJob extends AbstractDownloadJob{

	public MockDownloadJob() {
		super("", "", "","");
	}

	@Override
	public void run() {
	}

	@Override
	public void cancel() {
		downloadJobState=DownloadJobState.CANCELLED;
	} 
	
}