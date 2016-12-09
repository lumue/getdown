package io.github.lumue.getdown.core.common.persistence;

public interface ObjectBuilder<T> {
	ObjectBuilder<T> withKey(String keyValue);
	T build();


}
