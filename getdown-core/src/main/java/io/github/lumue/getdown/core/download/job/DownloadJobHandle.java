package io.github.lumue.getdown.core.download.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by lm on 30.10.16.
 */
public class DownloadJobHandle implements Serializable {

	private static final long serialVersionUID = -7582907691952041979L;

	@JsonProperty("key")
	private String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public DownloadJobHandle() {
		this(UUID.randomUUID().toString());
	}

	@JsonCreator
	public DownloadJobHandle(@JsonProperty("key") String key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DownloadJobHandle other = (DownloadJobHandle) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return key;
	}

}
