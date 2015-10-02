package io.github.lumue.getdown.hosts.streamcloud;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;

import io.github.lumue.getdown.core.download.conversation.JobConversations;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.lumue.getdown.core.download.resolver.ContentLocation;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolver;
import sun.plugin2.message.Conversation;

@Named
public class StreamcloudContentLocationResolver implements ContentLocationResolver {

	private final static String[] HOSTS = { "streamcloud.eu" };

	static Logger LOGGER = LoggerFactory
			.getLogger(StreamcloudContentLocationResolver.class);

	private final JobConversations conversations;

	@Inject
	public StreamcloudContentLocationResolver(JobConversations conversations) {
		this.conversations = conversations;
	}

	@Override
	public ContentLocation resolve(DownloadJob job) throws IOException {

		final String url=job.getUrl();
		LOGGER.debug("resolving download location for "+url);


		Conversation conversations.get(job);

		
		wait(15);

		String mediaPlayerPageContent = conversations
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
