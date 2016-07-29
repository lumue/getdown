package io.github.lumue.getdown.core.common.persistence;


import io.github.lumue.getdown.core.download.job.DownloadJob;

import java.util.*;
import java.util.stream.Stream;

/**
 * not thread safe, in memory repository
 * 
 * @author lm
 *
 */
public class MapBasedObjectRepository<B extends ObjectBuilder<V>, K, V extends HasIdentity<K>> implements ObjectRepository<B, K, V> {

	protected final Map<K, V> objectMap;

	public MapBasedObjectRepository(Map<K, V> jobMap) {
		super();
		this.objectMap = jobMap;
	}

	MapBasedObjectRepository() {
		this(new HashMap<>());
	}

	@Override
	public V create(B builder) {
		V job = builder.build();
		objectMap.put(job.getHandle(), job);
		return job;
	}

	@Override
	public Collection<V> list() {
		ArrayList<V> list = new ArrayList<>();
		list.addAll( objectMap.values());
		return java.util.Collections.unmodifiableList(list);
	}


	@Override
	public Stream<V> stream() {
		return objectMap.values().stream();
	}



	@Override
	public void remove(K handle) {
		objectMap.remove(handle);
	}

	@Override
	public V get(K handle) {
		return objectMap.get(handle);
	}

	@Override
	public void update(V value) {
		objectMap.replace(value.getHandle(),value);
	}

	protected Map<K, V> getMap() {
		return objectMap;
	}

	@Override
	public void close() throws Exception {

	}
}
