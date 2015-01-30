package io.github.lumue.getdown.hosts.streamcloud;

import io.github.lumue.getdown.resolver.ContentLocation;
import io.github.lumue.getdown.resolver.ContentLocationResolver;
import io.github.lumue.getdown.util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Named;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

@Named
public class StreamcloudContentLocationResolver implements ContentLocationResolver {
	
	private final static String[] HOSTS = { "streamcloud.eu" };

	private static Logger LOGGER = LoggerFactory.getLogger(StreamcloudContentLocationResolver.class);

	private HttpClient client = new DefaultHttpClient();



	@Override
	public ContentLocation resolve(String url) throws IOException {


		String sessionId = createStreamcloudSessionId(url);

		wait(11);

		String mediaPlayerPageContent = loadMediaPlayerPage(sessionId, url);

		String contentUrl = scrapePageForContentUrl(mediaPlayerPageContent);

		return new ContentLocation(contentUrl, StreamcloudUrlParser.fromUrl(url).getFilename());
	}



	private String scrapePageForContentUrl(String viewerPageContent) throws JsonProcessingException, IOException {

		LOGGER.debug("parse streamcloud mediaplayer page for content url");

		Document viewerPageDocument = Jsoup.parse(viewerPageContent);

		Elements mediaPlayerScriptElements = viewerPageDocument.getElementsByTag("script");

		Element mediaPlayerScriptElement = mediaPlayerScriptElements.stream()
				.filter(scriptElement -> scriptElement.toString().contains("jwplayer(\"mediaplayer\").setup"))
				.findFirst().get();

		String mediaPlayerScript = mediaPlayerScriptElement.childNode(0).toString();

		int mediaplayerJsonStart = mediaPlayerScript.indexOf("{");
		int mediaplayerJsonEnd = mediaPlayerScript.indexOf("}");
		String mediaPlayerJsonString = mediaPlayerScript.substring(mediaplayerJsonStart, mediaplayerJsonEnd + 1);
		int indexOfTrailingComma = mediaPlayerJsonString.lastIndexOf(",");
		mediaPlayerJsonString = mediaPlayerScript.substring(mediaplayerJsonStart, indexOfTrailingComma) + "\"}";
		Map<String, Object> mediaPlayerAttributes = JsonUtil.parseJson(mediaPlayerJsonString);

		String contentUrl = (String) mediaPlayerAttributes.get("file");
		LOGGER.debug("parsed content url" + contentUrl + " from streamcloud mediaplayer page");
		return contentUrl;
	}



	

	private String createStreamcloudSessionId(String url) throws IOException {

		LOGGER.debug("load page at " + url + " to retrieve session cookie");

		HttpGet request = new StreamcloudPageGetRequest(url);

		LOGGER.debug("\nSending 'GET' request to URL : " + url);
		HttpResponse response = client.execute(request);

		String sessionCookie = response.getFirstHeader("Set-Cookie").toString();

		request.abort();


		return sessionCookie;

	}

	private void wait(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			LOGGER.error(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	private String loadMediaPlayerPage(String sessionId, String url) throws IOException {

		LOGGER.debug("post request to " + url + " with sessionId " + sessionId + " to get media player page");

		HttpPost post = new StreamcloudFormPostRequest(url, sessionId);

		LOGGER.debug("\nSending 'POST' request to URL : " + url);
		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	@Override
	public Iterator<String> hosts() {
		return Arrays.asList(HOSTS).iterator();
	}
	

}
