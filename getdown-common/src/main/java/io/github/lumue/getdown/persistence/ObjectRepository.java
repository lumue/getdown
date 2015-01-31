package io.github.lumue.getdown.persistence;

import java.util.Collection;

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
public interface ObjectRepository<B, K, V> {
	public V create(B builder);

	public Collection<V> list();

	public void remove(K handle);

	public V get(K handle);
}
