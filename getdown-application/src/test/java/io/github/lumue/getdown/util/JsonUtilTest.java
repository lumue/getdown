package io.github.lumue.getdown.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonUtilTest {

	private final static String JSON_STRING = "{\n" +
			"	provider: \"http\",\n" +
			"	flashplayer: \"http://streamcloud.eu/player/player.swf\",\n" +
			"	file: \"http://stor39.streamcloud.eu:8080/53v75bqwnwoax3ptxycylqv7vegvuifexh4vweacsms6cf5m7tocsilble/video.mp4\",\n" +
			"	image: \"http://stor39.streamcloud.eu:8080/i/01/00844/w7l5g5enduu9.jpg\",\n" +
			"	height: 537,\n" +
			"	width: 900,\n" +
			"	abouttext: \"Help\",\n" +
			"	aboutlink: \"http://streamcloud.eu//playerhelp.html\",\n" +
			"	startparam: \"start\",\n" +
			"	skin: \"http://streamcloud.eu/player/skin.xml\"" +
			" \n" +
			"}";

	@Test
	public void testParseJson() throws JsonProcessingException, IOException {

		Map<String, Object> result = JsonUtil.parseJson(JSON_STRING);

		assertEquals("http", result.get("provider"));

	}

}
