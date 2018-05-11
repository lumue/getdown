package io.github.lumue.getdown.core.download.job;

import java.io.Serializable;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.lumue.getdown.core.common.persistence.HasIdentity;
import io.github.lumue.getdown.core.common.util.Observable;
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public interface DownloadJob extends HasIdentity<String>,Serializable,Observable {

	@Override
	String getHandle();

	Optional<Throwable> getError();

	Optional<String> getMessage();

	Optional<DownloadProgress> getDownloadProgress();

	DownloadJobState getState();

	String getUrl();

	String getName();

	String getWorkPath();

	String getTargetPath();

	void prepare();

	void executeDownload();

	void postProcess();

	void cancel();

	/**
	 * sort order
	 * @return
	 */
	Long getIndex();

	boolean isPrepared();

	void waiting();

	enum DownloadJobState {
		WAITING,PREPARING,RUNNING, ERROR, FINISHED, CANCELLED, PREPARED;
	}

}
