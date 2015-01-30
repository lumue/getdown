package io.github.lumue.getdown.hosts.streamcloud;

import org.apache.http.client.methods.HttpGet;

class StreamcloudPageGetRequest extends HttpGet {

	private final static String USER_AGENT = "Mozilla/5.0";

	StreamcloudPageGetRequest(String uri) {
		super(uri);
		this.setHeader("User-Agent", USER_AGENT);
		this.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		this.setHeader("Accept-Language", "en-US,en;q=0.5");
	}

}
