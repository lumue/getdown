
package io.github.lumue.getdown.hosts.xhamster;

import io.github.lumue.getdown.core.download.resolver.ContentLocation;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;

import javax.inject.Named;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

@Named
public class XhamsterContentLocationResolver implements ContentLocationResolver {

	private static Logger LOGGER = LoggerFactory.getLogger(XhamsterContentLocationResolver.class);

	private final static String[] HOSTS = { "xhamster.com" };

	private HttpClient client = new DefaultHttpClient();

	@Override
	public ContentLocation resolve(String url) throws IOException {
		String mediaPlayerPageContent = loadMediaPlayerPage(url);
		String contentUrl = scrapePageForContentUrl(mediaPlayerPageContent);
		int lastSlash = url.lastIndexOf("/");
		int lastDot = url.lastIndexOf(".");
		String filename = url.substring(lastSlash + 1, lastDot);
		lastDot = contentUrl.lastIndexOf(".");
		String filenameSuffix = contentUrl.substring(lastDot);
		filename = filename + filenameSuffix;
		return new ContentLocation(contentUrl, filename);
	}

	private String loadMediaPlayerPage(String url) throws IOException {
		HttpGet getRequest = new HttpGet(url);

		LOGGER.debug("\nSending 'GET' request to URL : " + url);
		HttpResponse response = client.execute(getRequest);
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

	private String scrapePageForContentUrl(String viewerPageContent) throws JsonProcessingException, IOException {

		LOGGER.debug("parse xhamster mediaplayer page for content url");

		Document viewerPageDocument = Jsoup.parse(viewerPageContent);
		Element videoElement = viewerPageDocument.getElementsByTag("video").first();
		String contentUrl = videoElement.attr("file");

		LOGGER.debug("parsed content url" + contentUrl + " from xhamster mediaplayer page");

		return contentUrl;
	}
}
