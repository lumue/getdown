package io.github.lumue.getdown.hosts.streamcloud;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

class StreamcloudFormPostRequestHelper extends HttpPost {

	public static HttpPost setHeaders(HttpPost httpPost,String uri) throws UnsupportedEncodingException {
		// add header
		httpPost.setHeader("Host", "streamcloud.eu");
		httpPost.setHeader("User-Agent", USER_AGENT);
		httpPost.setHeader("Accept",
				" text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.8");
		// httpPost.setHeader("Cookie", sessionCookie);
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Referer", uri);
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setHeader("Cache-Control", "max-age=0");
		httpPost.setHeader("Origin", "http://streamcloud.eu");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");

		StreamcloudPostParameter postParams = new StreamcloudPostParameter(uri);
		httpPost.setEntity(new UrlEncodedFormEntity(postParams.asList()));
		return httpPost;
	}

	static class StreamcloudPostParameter {

		private final static List<NameValuePair> TEMPLATE_LIST = new ArrayList<NameValuePair>(5);

		static {
			TEMPLATE_LIST.add(new BasicNameValuePair("op", "download1"));
			TEMPLATE_LIST.add(new BasicNameValuePair("usr_login", ""));
			TEMPLATE_LIST.add(new BasicNameValuePair("hash", ""));
			TEMPLATE_LIST.add(new BasicNameValuePair("referer", ""));
			TEMPLATE_LIST.add(new BasicNameValuePair("imhuman", "Watch video now"));
		}

		private final List<NameValuePair> parameterList = new ArrayList<NameValuePair>(TEMPLATE_LIST);

		StreamcloudPostParameter(String url) {
			super();
			StreamcloudUrlParser streamcloudUrl = StreamcloudUrlParser.fromUrl(url);
			parameterList.add(new BasicNameValuePair("id", streamcloudUrl.id));
			parameterList.add(new BasicNameValuePair("fname", streamcloudUrl.filename));
		}

		public List<? extends NameValuePair> asList() {
			return Collections.unmodifiableList(parameterList);
		}

	}

	private final static String USER_AGENT = "Mozilla/5.0";

	StreamcloudFormPostRequestHelper(String uri) throws UnsupportedEncodingException {
		super(uri);

	}

}
