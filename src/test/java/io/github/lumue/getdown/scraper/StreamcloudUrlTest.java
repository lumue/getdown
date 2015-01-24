package io.github.lumue.getdown.scraper;

import static org.junit.Assert.assertEquals;
import io.github.lumue.getdown.scraper.StreamcloudUrlScraper.StreamcloudUrl;

import org.junit.Test;

public class StreamcloudUrlTest {

	@Test
	public void test() {
		StreamcloudUrl streamcloudUrl = StreamcloudUrl
				.fromUrl("http://streamcloud.eu/w7l5g5enduu9/The.Big.Bang.Theory.S08E10.720p.HDTV.X264-DIMENSION.mkv.html");
		assertEquals("wrong host", "streamcloud.eu", streamcloudUrl.getHost());
		assertEquals("wrong id", "w7l5g5enduu9", streamcloudUrl.getId());
		assertEquals("wrong filename", "streamcloud.eu", streamcloudUrl.getHost());
	}

}
