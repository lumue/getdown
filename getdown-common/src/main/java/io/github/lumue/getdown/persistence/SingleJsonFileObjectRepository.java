package io.github.lumue.getdown.persistence;

import io.github.lumue.getdown.util.JsonUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.util.Strings;



public abstract class SingleJsonFileObjectRepository<B extends ObjectBuilder<V>, K, V extends HasIdentity<K>> implements
		ObjectRepository<B, K, V> {

	private final String filename;

	private final Map<K, V> objectMap;

	private final static Logger LOGGER = LoggerFactory.getLogger(SingleJsonFileObjectRepository.class);

	ExecutorService flushExecutorService = Executors.newSingleThreadExecutor();


	public SingleJsonFileObjectRepository(String filename) {
		this.filename = filename + ".json";
		this.objectMap = new ConcurrentHashMap<K, V>();
		restore();
	}



	@Override
	public V create(B builder) {
		V job = builder.build();
		objectMap.put(job.getHandle(), job);
		triggerFlush();
		return job;
	}


	@Override
	public Collection<V> list() {
		return java.util.Collections.unmodifiableCollection(objectMap.values());
	}


	@Override
	public void remove(K handle) {
		objectMap.remove(handle);
		triggerFlush();
	}

	@Override
	public V get(K handle) {
		return objectMap.get(handle);
	}

	protected Map<K, V> getMap() {
		return objectMap;
	}

	private synchronized void restore() {
		
		if(!(new File(filename).exists()))
			return;
		
		String filecontent = loadContent();

		if (Strings.isNullOrEmpty(filecontent))
			return;

		try {

			TypeReference<V[]> typeReference = newTypeReference();
			List<V> objects = Arrays.asList(JsonUtil.deserializeBeans(typeReference, filecontent));
			objectMap.clear();
			objects.forEach(object -> objectMap.put(object.getHandle(), object));

		} catch (Exception e)
		{
			LOGGER.error("Object deserialisation failed during restore", e);
		}


	}



	protected abstract TypeReference<V[]> newTypeReference();



	private String loadContent() {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(filename));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		String filecontent=new String(encoded);
		
		return filecontent;
	}

	private synchronized void triggerFlush() {
		final String contentSnapshot = JsonUtil.serializeBeans(objectMap.values());
		flushExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				flush(contentSnapshot);
			}
		});
	}

	private synchronized void flush(final String contentSnapshot) {
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(filename));
			writer.write(contentSnapshot);
		} catch (IOException e)
		{
		} finally
		{
			try
			{
				if (writer != null)
					writer.close();
			} catch (IOException e)
			{
			}
		}
	}
}
