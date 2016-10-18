package io.github.lumue.getdown.core.download.job;

/**
 * simple string to string converter to apply pre download url modifications
 * Created by lm on 18.10.16.
 */
@FunctionalInterface
public interface UrlProcessor {
	String processUrl(String in);
}
