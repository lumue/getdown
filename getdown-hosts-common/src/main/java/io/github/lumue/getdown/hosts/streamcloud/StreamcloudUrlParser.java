package io.github.lumue.getdown.hosts.streamcloud;

import java.util.StringTokenizer;

public class StreamcloudUrlParser {

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

	final String id;
	
	final String filename;
	
	public static StreamcloudUrlParser fromUrl(String url)
	{
		StringTokenizer tokenizer=new StringTokenizer(url.replace("http://", ""), "/");
		String host = tokenizer.nextToken();
		String id = tokenizer.nextToken();
		String filename = tokenizer.nextToken().replace(".html", "");

		return new StreamcloudUrlParser(url, host, id, filename);
	}

	private StreamcloudUrlParser(String url, String host, String id, String filename) {
		super();
		this.host = host;
		this.id = id;
		this.filename = filename;
	}

}