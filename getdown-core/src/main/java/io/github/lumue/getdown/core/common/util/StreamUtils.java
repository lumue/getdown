package io.github.lumue.getdown.core.common.util;

import io.github.lumue.getdown.core.download.job.DownloadJob;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Convenience methods for dealing with java.util.stream
 * @author lm
 *
 */
public interface StreamUtils {
	static <T> Stream<T> stream(Iterable<T> in) {
	    return StreamSupport.stream(in.spliterator(), false);
	}

	static <T> Stream<T> stream(Iterator<T> source) {
		Iterable<T> iterable = () -> source;
		return stream(iterable);
	}
}
