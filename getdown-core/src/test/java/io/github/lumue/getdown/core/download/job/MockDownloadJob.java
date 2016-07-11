package io.github.lumue.getdown.core.download.job;

/**
 * A DownloadJob for testing
 * @author lm
 *
 */
@SuppressWarnings("serial")
public class MockDownloadJob extends Download implements DownloadJob{

	public MockDownloadJob() {
		super("", "", "","");
	}

	@Override
	public void prepare() {

	}

	@Override
	public void run() {
	}

	@Override
	public void cancel() {
		downloadJobState=DownloadJobState.CANCELLED;
	}

	@Override
	public Long getIndex() {
		return 0L;
	}

}