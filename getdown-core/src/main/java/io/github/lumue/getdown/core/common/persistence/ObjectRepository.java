package io.github.lumue.getdown.core.common.persistence;


import java.util.List;
import java.util.stream.Stream;

/**
 * 
 * interface for persistence strategies
 * 
 * @author lm
 *
 * @param <B>
 * @param <K>
 * @param <V>
 */
public interface ObjectRepository<B, K, V> extends AutoCloseable{
	V create(B builder);

	List<V> list();

	Stream<V> stream();

	void remove(K handle);

	V get(K handle);

	void update(V value);
}
