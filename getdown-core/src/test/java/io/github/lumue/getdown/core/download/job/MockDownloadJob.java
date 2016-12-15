package io.github.lumue.getdown.core.download.job;

/**
 * A DownloadJob for testing
 * @author lm
 *
 */
@SuppressWarnings("serial")
public class MockDownloadJob extends Download implements DownloadJob{

	MockDownloadJob() {
		super("", "", "","",0L,"");
	}

	@Override
	public void prepare() {

	}

	@Override
	public void executeDownload() {
	}

	@Override
	public void postProcess() {

	}

	@Override
	public void cancel() {
		downloadJobState=DownloadJobState.CANCELLED;
	}

	@Override
	public Long getIndex() {
		return 0L;
	}

	@Override
	public boolean isPrepared() {
		return true;
	}

}