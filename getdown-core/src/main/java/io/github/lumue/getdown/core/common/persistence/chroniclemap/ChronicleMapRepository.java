package io.github.lumue.getdown.core.common.persistence.chroniclemap;

import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.persistence.ObjectRepository;
import io.github.lumue.getdown.core.common.persistence.jdkmap.MapBasedObjectRepository;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import net.openhft.chronicle.map.ChronicleMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ChronicleMapRepository<B extends ObjectBuilder<V>, K, V extends HasIdentity<K>> extends MapBasedObjectRepository<B, K, V> {

	private final String filename;

	private final static Logger LOGGER = LoggerFactory.getLogger(ChronicleMapRepository.class);

	private final Class<K> keyType;
	private final Class<V> valueType;


	public ChronicleMapRepository(String filename, Class<K> keyType, Class<V> valueType, K averageKey, V averageValue) throws IOException {

		super(ChronicleMap
				.of(keyType, valueType)
				.averageKey(averageKey)
				.averageValue(averageValue)
				.entries(1000)
				.createPersistedTo(new File(filename))
		);
		this.filename = filename;
		this.keyType = keyType;
		this.valueType = valueType;
	}




	@Override
	public V create(B builder) {
		V job = builder.build();
		objectMap.put(job.getHandle(), job);
		return job;
	}





	public Class<K> getKeyType() {
		return keyType;
	}

	public Class<V> getValueType() {
		return valueType;
	}


}
