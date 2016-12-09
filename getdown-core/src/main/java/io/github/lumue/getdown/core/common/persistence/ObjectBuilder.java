package io.github.lumue.getdown.core.common.persistence;

public interface ObjectBuilder<T> {
	ObjectBuilder<T> withHandle(String keyValue);
	T build();


	boolean hasHandle();
}
