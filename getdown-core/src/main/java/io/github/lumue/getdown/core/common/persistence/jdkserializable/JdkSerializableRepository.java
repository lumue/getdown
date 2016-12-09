package io.github.lumue.getdown.core.common.persistence.jdkserializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.persistence.ObjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class JdkSerializableRepository<B extends ObjectBuilder<V>, K, V extends HasIdentity<K>> implements
		ObjectRepository<B, K, V>,
	AutoCloseable{

	private final String filename;

	private Map<K, byte[]> objectMap=new ConcurrentHashMap<>();

	private final static Logger LOGGER = LoggerFactory.getLogger(JdkSerializableRepository.class);

	private ExecutorService flushExecutorService = Executors.newSingleThreadExecutor();


	JdkSerializableRepository(String filename) {
		this.filename = filename ;
		restore();
	}

	@Override
	public Stream<V> stream() {
		return getValues().stream();
	}

	protected Collection<V> getValues() {
		return this.objectMap.values().stream()
				.map(this::deserializeObject)
				.collect(Collectors.toList());
	}

	private V deserializeObject(byte[] b) {
		if(b==null)
			return null;
		try {
			ObjectInputStream inputStream=new ObjectInputStream(new ByteArrayInputStream(b));
			return (V) inputStream.<V>readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] serializeObject(V object) {
		try {
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			return outputStream.toByteArray();
		}catch (IOException e)
		{
			LOGGER.error("Object serialisation failed", e);
			return null;
		}
	}


	@Override
	public V create(B builder) {
		builder.withHandle(UUID.randomUUID().toString());
		V o = builder.build();
		objectMap.put(o.getHandle(), serializeObject(o));
		triggerFlush();
		return o;
	}


	@Override
	public Collection<V> list() {
		ArrayList<V> list = new ArrayList<>();
		list.addAll(getValues());
		return java.util.Collections.unmodifiableList(list);
	}


	@Override
	public void remove(K handle) {
		objectMap.remove(handle);
		triggerFlush();
	}

	@Override
	public V get(K handle) {
		byte[] b = objectMap.get(handle);
		if(b==null)
			return null;
		return deserializeObject(b);
	}


	@SuppressWarnings("unchecked")
	private synchronized void restore() {
		
		if(!(new File(filename).exists()))
			return;
		
		byte[] contentSnapshot = loadContent();

		if (contentSnapshot==null)
			return;

		try {
			ObjectInputStream inputStream=new ObjectInputStream(new ByteArrayInputStream(contentSnapshot));
			objectMap=(ConcurrentHashMap<K, byte[]>) inputStream.readObject();
			inputStream.close();
		} catch (Exception e)
		{
			LOGGER.error("Object deserialisation failed during restore", e);
		}


	}


	@Override
	public void update(V value) {
		objectMap.put(value.getHandle(),serializeObject(value));
		this.triggerFlush();
	}



	private byte[] loadContent() {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(filename));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return encoded;
	}

	private void triggerFlush() {
		final byte[] contentSnapshot = createContentSnapshot();

		if (contentSnapshot == null)
			return;

		flushExecutorService.submit(() -> flush(contentSnapshot));
	}

	private byte[] createContentSnapshot() {
		try {
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(objectMap);
			objectOutputStream.close();
			return outputStream.toByteArray();
		}catch (IOException e)
		{
			LOGGER.error("Object serialisation failed during createContentSnapshot", e);
			return null;
		}
	}



	private synchronized void flush(final byte[] contentSnapshot) {
		try
		{
			Files.write(Paths.get(filename), contentSnapshot);
		} catch (IOException e)
		{
			LOGGER.error("Object serialisation failed during flush", e);
		} 
	}

	@Override
	public void close() throws Exception {
		final byte[] contentSnapshot = createContentSnapshot();

		if (contentSnapshot == null)
			return;

		flush(contentSnapshot);
	}


}
