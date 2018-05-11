package io.github.lumue.getdown.core.download.job;

@FunctionalInterface
public interface ProgressionListener {
	void onProgress(String message,Progression p);
}
