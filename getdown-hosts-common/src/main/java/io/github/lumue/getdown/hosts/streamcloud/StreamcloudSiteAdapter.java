package io.github.lumue.getdown.hosts.streamcloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StreamcloudSiteAdapter {

	private static HttpClient STREAMCLOUD_HTTP_CLIENT = new DefaultHttpClient();

	private static Logger LOGGER = LoggerFactory
			.getLogger(StreamcloudSiteAdapter.class);

	static void acquireCookie(String url) throws IOException {



		LOGGER.debug("load page at " + url + " to retrieve session cookie");



		HttpGet request = new StreamcloudPageGetRequest(url);
		try {
			LOGGER.debug("\nSending 'GET' request to URL : " + url);

			// String sessionCookie = Objects
			// .requireNonNull(response.getFirstHeader("Set-Cookie"),
			// "no session cookie found in response headers")
			// .toString();
		} finally {
			request.abort();
		}

	}

	static String loadPageContent(String url)
			throws IOException {

		LOGGER.debug("post request to " + url + " to get media player page");

		HttpPost post = new StreamcloudFormPostRequest(url);

		LOGGER.debug("\nSending 'POST' request to URL : " + url);
		HttpResponse response = STREAMCLOUD_HTTP_CLIENT.execute(post);
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
