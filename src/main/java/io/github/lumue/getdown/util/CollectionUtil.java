package io.github.lumue.getdown.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionUtil {
	public static <T> Stream<T> stream(Iterable<T> in) {
	    return StreamSupport.stream(in.spliterator(), false);
	}
}
