package io.github.lumue.getdown.hosts.streamcloud;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lumue.getdown.core.download.resolver.ContentLocation;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolver;

@Named
public class StreamcloudContentLocationResolver implements ContentLocationResolver {

	private final static String[] HOSTS = { "streamcloud.eu" };

	static Logger LOGGER = LoggerFactory
			.getLogger(StreamcloudContentLocationResolver.class);




	@Override
	public ContentLocation resolve(String url) throws IOException {
		
		LOGGER.debug("resolving download location for "+url);

		StreamcloudSiteAdapter.acquireCookie(url);

		
		wait(15);

		String mediaPlayerPageContent = StreamcloudSiteAdapter
				.loadPageContent(url);

		String contentUrl = StreamcloudPageScraper.scrapePageContentForDownloadUrl(mediaPlayerPageContent);

		return new ContentLocation(contentUrl, StreamcloudUrlParser.fromUrl(url).getFilename());
	}


	private void wait(int seconds) {
		try {
			LOGGER.debug("waiting "+seconds+" seconds");
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			LOGGER.error(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}



	@Override
	public Iterator<String> hosts() {
		return Arrays.asList(HOSTS).iterator();
	}


}
