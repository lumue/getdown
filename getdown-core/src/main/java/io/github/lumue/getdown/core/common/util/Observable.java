package io.github.lumue.getdown.core.common.util;

/**
 * Something to be observed :)
 * @see java.util.Observable and the observable design pattern
 * 
 * java.util.Observable is not used because it is a class, which is not nice
 * 
 * @author lm
 *
 */
public interface Observable {
	<T extends Observable> T addObserver(Observer<T> observer);
	<T extends Observable> T removeObserver(Observer<T> observer);
	<T extends Observable> T removeObservers();
}
