package io.github.lumue.getdown.core.download.job;

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
	 * resolve location(s) for a DownloadJob
	 * 
	 * @param url
	 * @return content url
	 * @throws IOException
	 */
	public ContentLocation resolve(DownloadJob job) throws IOException;

	/**
	 * host(s) handled by this resolver
	 * 
	 * @return
	 */
	public Iterator<String> hosts();
}
