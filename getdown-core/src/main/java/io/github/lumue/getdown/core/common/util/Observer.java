package io.github.lumue.getdown.core.common.util;

@FunctionalInterface
public interface Observer<T extends Observable> {
	
	/**
	 * called by observable when its status has changed
	 * @param topic
	 * @param observable
	 */
	void onUpdate(String topic, T observable);
}
