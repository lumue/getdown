package io.github.lumue.getdown.core.download.job;

@FunctionalInterface
public interface ErrorListener {
	
	void onError(String message, Throwable error);
	
}
