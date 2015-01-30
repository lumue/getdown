package io.github.lumue.getdown.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Convenience methods for dealing with java.util.stream
 * @author lm
 *
 */
public interface StreamUtils {
	public static <T> Stream<T> stream(Iterable<T> in) {
	    return StreamSupport.stream(in.spliterator(), false);
	}
}
