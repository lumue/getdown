package io.github.lumue.getdown.core.download.task;

public abstract class ValidateTaskJob implements Runnable {
	
	private final DownloadTask task;
	
	protected ValidateTaskJob(DownloadTask task) {
		this.task = task;
	}
	
	public DownloadTask getTask() {
		return task;
	}
}
