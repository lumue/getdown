package io.github.lumue.getdown.core.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * base implementation for observables. 
 * @author lm
 *
 */
public  class ObservableTemplate implements Observable{

	@FunctionalInterface
	protected interface ObservedStateChange {
		void doObserved();
	}
	
	@SuppressWarnings("rawtypes")
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
	
	protected void doObserved(String topic,ObservedStateChange observedStateChange){
		observedStateChange.doObserved();
		notifyObservers(topic);
	}
	
	
	private void notifyObservers(String topic){
		observers.forEach(observer -> observer.onUpdate(topic,this));
	}

}
