package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.common.persistence.ObjectBuilder;
import io.github.lumue.getdown.core.common.persistence.ObjectRepository;
import io.github.lumue.getdown.core.download.job.Download.DownloadJobState;
import io.github.lumue.getdown.core.download.job.Download.DownloadJobHandle;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DownloadJobRepository extends ObjectRepository<ObjectBuilder< DownloadJob>, DownloadJobHandle, DownloadJob> {


	/**
	 * @param state {@link DownloadJobState} to filter
	 * @return job with state state, all jobs if state is null
	 */
	default List<DownloadJob> findByJobState(DownloadJobState state) {

		if(state==null)
			return list();

		return streamByJobState(state)
				.collect(Collectors.toList());
	}

    default Stream<DownloadJob> streamByJobState(DownloadJobState state){

	    if(state==null)
		    return stream();

	    return stream()
			    .filter(job -> state.equals(job.getState()));
    };
}
