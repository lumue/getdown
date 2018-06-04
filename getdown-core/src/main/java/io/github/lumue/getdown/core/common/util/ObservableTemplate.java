package io.github.lumue.getdown.core.common.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * base implementation for observables. 
 * @author lm
 *
 */
public class ObservableTemplate<T extends Observable> implements Observable{
	
	

	private final T observable;

	public ObservableTemplate(T observable) {
		this.observable = observable;
	}

	public T getObservable() {
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
	public <R extends Observable> R addObserver(Observer<R> observer) {
		if(!observers.contains(observer))
			observers.add(observer);
		return (R) this.observable;
	}

	@Override
	public <R extends Observable> R removeObserver(Observer<R> observer) {
		observers.remove(observer);
		return (R) this.observable;
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
