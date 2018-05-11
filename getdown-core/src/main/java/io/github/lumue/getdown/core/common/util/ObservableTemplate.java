package io.github.lumue.getdown.core.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * base implementation for observables. 
 * @author lm
 *
 */
public class ObservableTemplate implements Observable{
	
	

	private final Observable observable;

	public ObservableTemplate(Observable observable) {
		this.observable = observable;
	}

	public Observable getObservable() {
		return observable;
	}


	@FunctionalInterface
	public interface ObservedStateChange {
		void doObserved();
	}
	
	@SuppressWarnings("rawtypes")
	@JsonIgnore
	private final List<Observer> observers=new ArrayList<>();
	
	@Override
	public synchronized Observable addObserver(Observer<?> observer) {
		if(!observers.contains(observer))
			observers.add(observer);
		return this;
	}

	@Override
	public synchronized Observable removeObserver(Observer<?> observer) {
		observers.remove(observer);
		return this;
	}
	
	@Override
	public synchronized Observable removeObservers() {
		observers.clear();
		return this;
	}
	
	public void doObserved(ObservedStateChange observedStateChange){
		observedStateChange.doObserved();
		notifyObservers();
	}


	private void notifyObservers() {
		observers.forEach(observer -> observer.onUpdate(getObservable()));
	}

}
