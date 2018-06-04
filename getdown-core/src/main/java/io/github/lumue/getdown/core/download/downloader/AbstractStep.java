package io.github.lumue.getdown.core.download.downloader;

import io.github.lumue.getdown.core.common.util.Observable;
import io.github.lumue.getdown.core.common.util.ObservableTemplate;
import io.github.lumue.getdown.core.common.util.Observer;
import io.github.lumue.getdown.core.download.job.Progression;

public abstract class AbstractStep implements Observable {
	private final ObservableTemplate<? extends AbstractStep> observableTemplate = new ObservableTemplate<>(this);
	private Progression progression;
	
	protected abstract void execute();
	
	void initProgression(long totalExpectedSize) {
		observableTemplate.doObserved(() ->
			progression = new Progression(0, totalExpectedSize)
		);
	}
	
	void incrementProgression(long resumeAt) {
		observableTemplate.doObserved(()->
		    progression.incrementProgress(resumeAt)
		);
	}
	
	@Override
	public Observable removeObservers() {
		observableTemplate.removeObservers();
		return this;
	}
	
	Progression getProgression() {
		return progression;
	}
	
	@Override
	public <T extends Observable> T addObserver(Observer<T> observer) {
		return observableTemplate.addObserver(observer);
	}
	
	@Override
	public <T extends Observable> T removeObserver(Observer<T> observer) {
		return observableTemplate.removeObserver(observer);
	}
}
