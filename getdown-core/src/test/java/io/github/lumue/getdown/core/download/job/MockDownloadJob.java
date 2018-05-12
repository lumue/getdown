package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.download.task.DownloadTask;
import io.github.lumue.getdown.core.download.task.TaskState;

import java.time.LocalDateTime;

/**
 * A DownloadJob for testing
 * @author lm
 *
 */
@SuppressWarnings("serial")
public class MockDownloadJob extends AbstractDownloadJob implements DownloadJob{
	
	public MockDownloadJob() {
		super("", "", DownloadJobState.WAITING,new DownloadProgress(),"","",0L,"",new DownloadTask(TaskState.SUBMITTED,"","",LocalDateTime.now()),"");
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