package io.github.lumue.getdown.hosts.streamcloud;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StreamcloudGetRequestHelper {

	private final static String USER_AGENT = "Mozilla/5.0";

	private final static Map<String,String> HEADER_MAP;
	private static final List<MediaType> ACCEPT_LIST;

	static{
		HEADER_MAP=new HashMap<>();

		HEADER_MAP.put("User-Agent", USER_AGENT);
		HEADER_MAP.put("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		HEADER_MAP.put("Accept-Language", "en-US,en;q=0.5");

		ACCEPT_LIST=new ArrayList<>();
		ACCEPT_LIST.addAll(MediaType.parseMediaTypes(HEADER_MAP.get("Accept")));
	}

	static HttpHeaders headers() {
		HttpHeaders headers=new HttpHeaders();
		headers.setAccept(ACCEPT_LIST);
		return headers;
	}

}
