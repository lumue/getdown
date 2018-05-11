package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.download.task.DownloadTask;

/**
 * A DownloadJob for testing
 * @author lm
 *
 */
@SuppressWarnings("serial")
public class MockDownloadJob extends AbstractDownloadJob implements DownloadJob{
	
	
	protected MockDownloadJob(String url, String handle, DownloadJobState downloadJobState, DownloadProgress downloadProgress, String name, String host, Long index, String targetPath, DownloadTask downloadTask) {
		super(url, handle, downloadJobState, downloadProgress, name, host, index, targetPath, downloadTask);
	}
	
	public MockDownloadJob(String name, String url, String host, String handle, Long index, String targetPath, DownloadTask downloadTask) {
		super(name, url, host, handle, index, targetPath, downloadTask);
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