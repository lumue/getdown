package io.github.lumue.getdown.hosts.streamcloud;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.github.lumue.getdown.core.common.util.JsonUtil;

class StreamcloudPageScraper{

	static Logger LOGGER = LoggerFactory
			.getLogger(StreamcloudPageScraper.class);

	static String scrapePageContentForDownloadUrl(String pageContent) throws JsonProcessingException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"scraping streamcloud mediaplayer page for content url");
			LOGGER.debug("\n" + pageContent + "\n");
		}

		Document viewerPageDocument = Jsoup.parse(pageContent);

		Elements mediaPlayerScriptElements = viewerPageDocument.getElementsByTag("script");

		Element mediaPlayerScriptElement = mediaPlayerScriptElements.stream()
				.filter(scriptElement -> scriptElement.toString().contains("jwplayer(\"mediaplayer\").setup"))
				.findFirst().get();

		String mediaPlayerScript = mediaPlayerScriptElement.childNode(0).toString();

		int mediaplayerJsonStart = mediaPlayerScript.indexOf("{");
		int mediaplayerJsonEnd = mediaPlayerScript.indexOf(",//	skin");
		String mediaPlayerJsonString = mediaPlayerScript.substring(mediaplayerJsonStart, mediaplayerJsonEnd )+"}";
		Map<String, Object> mediaPlayerAttributes = JsonUtil.parseJson(mediaPlayerJsonString);

		String contentUrl = (String) mediaPlayerAttributes.get("file");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("parsed content url" + contentUrl
					+ " from streamcloud mediaplayer page");
		}

		return contentUrl;

	}
}