package io.github.lumue.getdown.core.download.task;

import io.github.lumue.getdown.core.common.util.Observable;

public abstract class ValidateTaskJob implements Runnable, Observable {
	
	private final DownloadTask task;
	
	protected ValidateTaskJob(DownloadTask task) {
		this.task = task;
	}
	
	public DownloadTask getTask() {
		return task;
	}
	
	public abstract ValidateTaskJob removeObservers();
}
