package io.github.lumue.getdown.scraper;

import io.github.lumue.getdown.util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamcloudUrlScraper implements UrlScraper {
	
	public static class StreamcloudUrl{

		public String getHost() {
			return host;
		}

		public String getId() {
			return id;
		}

		public String getFilename() {
			return filename;
		}

		private final String host;

		private final String id;
		
		private final String filename;
		
		public static StreamcloudUrl fromUrl(String url)
		{
			StringTokenizer tokenizer=new StringTokenizer(url.replace("http://", ""), "/");
			String host = tokenizer.nextToken();
			String id = tokenizer.nextToken();
			String filename = tokenizer.nextToken().replace(".html", "");

			return new StreamcloudUrl(host, id, filename);
		}

		private StreamcloudUrl(String host, String id, String filename) {
			super();
			this.host = host;
			this.id = id;
			this.filename = filename;
		}


	}
	
	
	private static Logger LOGGER = LoggerFactory.getLogger(StreamcloudUrlScraper.class);

	private HttpClient client = new DefaultHttpClient();

	private final static String USER_AGENT = "Mozilla/5.0";

	@Override
	public String scrape(String url) throws IOException {

		String sessionId = executeGetRequest(url);

		wait(11);

		String viewerPageContent = executePostRequest(sessionId, url);

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
		return (String) mediaPlayerAttributes.get("file");
	}



	

	private String executeGetRequest(String url) throws IOException {

		// build request
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "en-US,en;q=0.5");

		// execute request
		LOGGER.debug("\nSending 'GET' request to URL : " + url);
		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();
		LOGGER.debug("Response Code : " + responseCode);

		request.abort();

		return (response.getFirstHeader("Set-Cookie") == null) ? "" :
				response.getFirstHeader("Set-Cookie").toString();

	}

	private void wait(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			LOGGER.error(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
	}

	private String executePostRequest(String sessionId, String url) throws IOException {
		HttpPost post = new HttpPost(url);

		// add header
		post.setHeader("Host", "streamcloud.eu");
		post.setHeader("User-Agent", USER_AGENT);
		post.setHeader("Accept",
				" text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		post.setHeader("Accept-Language", "en-US,en;q=0.8");
		post.setHeader("Cookie", sessionId);
		post.setHeader("Connection", "keep-alive");
		post.setHeader("Referer", url);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("Cache-Control", "max-age=0");
		post.setHeader("Origin", "http://streamcloud.eu");
		post.setHeader("Accept-Encoding", "gzip, deflate");

		// create post parameter
		StreamcloudUrl streamcloudUrl = StreamcloudUrl.fromUrl(url);
		List<NameValuePair> postParams = new ArrayList<NameValuePair>(7);
		postParams.add(new BasicNameValuePair("op", "download1"));
		postParams.add(new BasicNameValuePair("usr_login", ""));
		postParams.add(new BasicNameValuePair("id", streamcloudUrl.id));
		postParams.add(new BasicNameValuePair("fname", streamcloudUrl.filename));
		postParams.add(new BasicNameValuePair("hash", ""));
		postParams.add(new BasicNameValuePair("referer", ""));
		postParams.add(new BasicNameValuePair("imhuman", "Watch video now"));

		post.setEntity(new UrlEncodedFormEntity(postParams));

		HttpResponse response = client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	

}
