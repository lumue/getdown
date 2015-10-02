package io.github.lumue.getdown.core.download.conversation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * register and lookup {@link ConversationFactory} s
 * 
 * @author lm
 *
 */
public class ConversationFactoryRegistry implements Iterable<ConversationFactory> {

	private Map<String, ConversationFactory> conversationFactoryMap = new HashMap<String, ConversationFactory>();
	private final static Logger LOGGER = LoggerFactory.getLogger(ConversationFactoryRegistry.class);

	public ConversationFactory lookup(String host) {
		LOGGER.debug("looking up ConversationFactory for " + host);
		ConversationFactory conversationFactory = conversationFactoryMap.get(host);
		if (conversationFactory == null) {
			LOGGER.warn("No ConversationFactory found for " + host + ". returning null");
		}
		else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("returning ConversationFactory " + conversationFactory.getClass().getCanonicalName() + " for " + host);
		}
		return conversationFactory;
	}

	public void register(ConversationFactory resolver) {
		resolver.hosts().forEachRemaining(host -> registerResolverForHost(host, resolver));
	}

	private void registerResolverForHost(String host, ConversationFactory resolver) {
		conversationFactoryMap.put(host, resolver);
		LOGGER.debug("registered " + resolver.getClass().getCanonicalName() + " for host " + host);
	}

	@Override
	public Iterator<ConversationFactory> iterator() {
		return conversationFactoryMap.values().iterator();
	}

}
