package io.github.lumue.getdown.core.download.downloader.internal;

import io.github.lumue.getdown.core.download.job.DownloadJob;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manage
 *
 * Created by lm on 09.09.15.
 */
public class JobConversations {

	private final ConversationFactoryRegistry factoryRegistry;

	private final ConcurrentMap<DownloadJob.DownloadJobHandle,Conversation> downloadJobHandleConversationMap=new ConcurrentHashMap<>();

	public JobConversations(ConversationFactoryRegistry factoryRegistry) {
		this.factoryRegistry = factoryRegistry;
	}

	/**
	 * return existing or create new Conversation
	 * @param job
	 * @return
	 */
	public Conversation get(DownloadJob job){
		return downloadJobHandleConversationMap.computeIfAbsent(job.getHandle(),(key) ->
		{
			final ConversationFactory f = factoryRegistry.lookup(job.getHost());
			return f.create();
		});

	}
}
