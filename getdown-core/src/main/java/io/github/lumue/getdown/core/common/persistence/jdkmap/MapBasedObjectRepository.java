package io.github.lumue.getdown.core.common.persistence.jdkmap;


import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.persistence.ObjectRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public List<V> list() {
		return java.util.Collections.unmodifiableList(new ArrayList<>(objectMap.values()));
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

	protected Map<K, V> getMap() {
		return objectMap;
	}

}
