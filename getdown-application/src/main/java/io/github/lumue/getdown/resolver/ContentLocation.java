package io.github.lumue.getdown.resolver;

import java.io.Serializable;


/**
 * 
 * describe a content location
 * 
 * @author lm
 *
 */
public class ContentLocation implements Serializable {

	private final String url;

	private final String filename;

	/**
	 * actual content location
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * a filename, in case the filename at url is generic (e.g. video.mp4)
	 */
	public String getFilename() {
		return filename;
	}

	public ContentLocation(String url, String filename) {
		this.url = url;
		this.filename = filename;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + this.url + "," + this.filename + ")";
	}

}
