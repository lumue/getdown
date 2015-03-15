package io.github.lumue.getdown.core.download.resolver;

import java.io.IOException;
import java.util.Iterator;

/**
 * resolve content locations for a given url on specific hosts
 * 
 * @author lm
 *
 */
public interface ContentLocationResolver {

	/**
	 * resolve location(s) for an url
	 * 
	 * @param url
	 * @return content url
	 * @throws IOException
	 */
	public ContentLocation resolve(String url) throws IOException;

	/**
	 * host(s) handled by this resolver
	 * 
	 * @return
	 */
	public Iterator<String> hosts();
}
