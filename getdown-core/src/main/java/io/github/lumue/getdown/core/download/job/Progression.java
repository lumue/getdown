package io.github.lumue.getdown.core.download.job;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Progression implements Serializable {
	private LocalDateTime timestamp=LocalDateTime.now();
	private long value;
	private final long max;
	
	public Progression( long value, long max) {
		this.value = value;
		this.max = max;
	}
	
	public Progression incrementProgress(long value){
		this.value+=value;
		timestamp=LocalDateTime.now();
		return this;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public long getValue() {
		return value;
	}
	
	public long getMax() {
		return max;
	}
}
