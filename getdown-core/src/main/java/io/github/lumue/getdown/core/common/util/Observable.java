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
	public Observable addObserver(Observer<?> observer);
	public Observable removeObserver(Observer<?> observer);
}
