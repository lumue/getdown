package io.github.lumue.getdown.core.download.downloader.internal;


import java.util.Iterator;

/**
 * use only via {@link JobConversations}. no need to use this elsewhere
 *
 * implement for each download host
 * implement host specific operations in returned {@link Conversation} instances
 *
 * Created by lm on 09.09.15.
 */
interface ConversationFactory {

	Conversation create();

	/**
	 * host(s) handled by returned conversations
	 *
	 * @return
	 */
	public Iterator<String> hosts();
}
