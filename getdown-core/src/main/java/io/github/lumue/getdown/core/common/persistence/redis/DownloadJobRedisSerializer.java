package io.github.lumue.getdown.core.common.persistence.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

/**
 * Created by lm on 10.07.16.
 */
public class DownloadJobRedisSerializer implements org.springframework.data.redis.serializer.RedisSerializer<DownloadJob> {

	private final ObjectMapper objectMapper;

	public DownloadJobRedisSerializer( ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public byte[] serialize(DownloadJob downloadJob) throws SerializationException {
		try {
			return objectMapper.writeValueAsBytes(downloadJob);
		} catch (JsonProcessingException e) {
			throw new SerializationException("error serializing "+downloadJob,e);
		}
	}

	@Override
	public DownloadJob deserialize(byte[] bytes) throws SerializationException {
		try {
			return objectMapper.readValue(bytes,DownloadJob.class);
		} catch (IOException e) {
			throw new SerializationException("error deserializing "+bytes,e);
		}
	}
}
