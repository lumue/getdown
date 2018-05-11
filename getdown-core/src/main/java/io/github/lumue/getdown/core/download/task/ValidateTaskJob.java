package io.github.lumue.getdown.core.download.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.lumue.getdown.core.common.util.Observable;
import io.github.lumue.getdown.core.common.util.ObservableTemplate;
import io.github.lumue.getdown.core.common.util.Observer;

public abstract class ValidateTaskJob implements Runnable, Observable {
	
	private final DownloadTask task;
	
	
	@JsonIgnore
	private ObservableTemplate observableTemplate=new ObservableTemplate(this);
	
	protected ValidateTaskJob(DownloadTask task) {
		this.task = task;
	}
	
	public DownloadTask getTask() {
		return task;
	}
	
	@Override
	public Observable removeObservers() {
		observableTemplate.removeObservers();
		return this;
	}
	
	@Override
	public Observable addObserver(Observer<?> observer) {
		return observableTemplate.addObserver(observer);
		
	}
	
	@Override
	public Observable removeObserver(Observer<?> observer) {
		return observableTemplate.removeObserver(observer);
	}
	
	protected void setInvalidState() {
		observableTemplate.doObserved(()->
				getTask().invalid()
		);
	}
	
	protected void setValidatedState() {
		observableTemplate.doObserved(()->
				getTask().validated()
		);
	}
	
	protected void setValidatingState() {
		observableTemplate.doObserved(()->
				getTask().validating()
		);
	}
}
