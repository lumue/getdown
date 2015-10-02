package io.github.lumue.getdown.hosts.streamcloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
class StreamcloudSiteAdapter {

	private final ClientHttpRequestFactory requestFactory;

	private static Logger LOGGER = LoggerFactory
			.getLogger(StreamcloudSiteAdapter.class);

	@Inject
	public StreamcloudSiteAdapter(ClientHttpRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	void acquireCookie(String url) throws IOException {



		LOGGER.debug("load page at " + url + " to retrieve session cookie");



		try {
			HttpGet request = StreamcloudGetRequestHelper.setHeaders(requestFactory.createRequest(URI.create(url), HttpMethod.GET));

			LOGGER.debug("\nSending 'GET' request to URL : " + url);

			// String sessionCookie = Objects
			// .requireNonNull(response.getFirstHeader("Set-Cookie"),
			// "no session cookie found in response headers")
			// .toString();
		} finally {
			request.abort();
		}

	}

	String loadPageContent(String url)
			throws IOException {

		LOGGER.debug("post request to " + url + " to get media player page");

		HttpPost post = StreamcloudFormPostRequestHelper.setHeaders(this.requestFactory.newPostRequest(url));

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
