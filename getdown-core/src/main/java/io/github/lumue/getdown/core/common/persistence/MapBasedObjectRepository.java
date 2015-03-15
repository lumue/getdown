package io.github.lumue.getdown.core.common.persistence;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class MapBasedObjectRepository<B extends ObjectBuilder<V>, K, V extends HasIdentity<K>> implements ObjectRepository<B, K, V> {

	private final Map<K, V> objectMap;

	public MapBasedObjectRepository(Map<K, V> jobMap) {
		super();
		this.objectMap = jobMap;
	}

	MapBasedObjectRepository() {
		this(new HashMap<K, V>());
	}

	@Override
	public V create(B builder) {
		V job = builder.build();
		objectMap.put(job.getHandle(), job);
		return job;
	}

	@Override
	public Collection<V> list() {
		return java.util.Collections.unmodifiableCollection(objectMap.values());
	}



	@Override
	public void remove(K handle) {
		objectMap.remove(handle);
	}

	@Override
	public V get(K handle) {
		return objectMap.get(handle);
	}

	protected Map<K, V> getMap() {
		return objectMap;
	}

}
