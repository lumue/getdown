package io.github.lumue.getdown.hosts.streamcloud;

import static org.junit.Assert.assertEquals;
import io.github.lumue.getdown.hosts.streamcloud.StreamcloudUrlParser;

import org.junit.Test;

public class StreamcloudUrlTest {

	@Test
	public void test() {
		StreamcloudUrlParser streamcloudUrl = StreamcloudUrlParser
				.fromUrl("http://streamcloud.eu/w7l5g5enduu9/The.Big.Bang.Theory.S08E10.720p.HDTV.X264-DIMENSION.mkv.html");
		assertEquals("wrong host", "streamcloud.eu", streamcloudUrl.getHost());
		assertEquals("wrong id", "w7l5g5enduu9", streamcloudUrl.getId());
		assertEquals("wrong filename", "The.Big.Bang.Theory.S08E10.720p.HDTV.X264-DIMENSION.mkv", streamcloudUrl.getFilename());
	}

}
