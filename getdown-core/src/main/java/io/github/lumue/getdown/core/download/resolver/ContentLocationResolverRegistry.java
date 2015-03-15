package io.github.lumue.getdown.core.download.resolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * register and lookup {@link ContentLocationResolver}
 * 
 * @author lm
 *
 */
public class ContentLocationResolverRegistry implements Iterable<ContentLocationResolver> {

	private Map<String, ContentLocationResolver> resolverMap = new HashMap<String, ContentLocationResolver>();
	private final static Logger LOGGER = LoggerFactory.getLogger(ContentLocationResolverRegistry.class);

	public ContentLocationResolver lookup(String host) {
		LOGGER.debug("looking up ContentLocationResolver for " + host);
		ContentLocationResolver contentLocationResolver = resolverMap.get(host);
		if (contentLocationResolver == null) {
			LOGGER.warn("No ContentLocationResolver found for " + host + ". returning null");
		}
		else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("returning ContentLocationResolver " + contentLocationResolver.getClass().getCanonicalName() + " for " + host);
		}
		return contentLocationResolver;
	}

	public void register(ContentLocationResolver resolver) {
		resolver.hosts().forEachRemaining(host -> registerResolverForHost(host, resolver));
	}

	private void registerResolverForHost(String host, ContentLocationResolver resolver) {
		resolverMap.put(host, resolver);
		LOGGER.debug("registered " + resolver.getClass().getCanonicalName() + " for host " + host);
	}

	@Override
	public Iterator<ContentLocationResolver> iterator() {
		return resolverMap.values().iterator();
	}

}
