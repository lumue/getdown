package io.github.lumue.getdown.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class JsonUtil {


	private static final JsonFactory JSON_FACTORY = new JsonFactory();
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	static {
		JSON_FACTORY.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
	}

	public static Map<String, Object> parseJson(String input) throws JsonProcessingException, IOException {
		ObjectReader reader = OBJECT_MAPPER.reader(Map.class);
		return reader.readValue(JSON_FACTORY.createParser(input));
	}
}
