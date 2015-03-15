package io.github.lumue.getdown.util;

import static org.junit.Assert.assertEquals;
import io.github.lumue.getdown.core.common.util.JsonUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

public class JsonUtilTest {

	public static class Adresse {
		private String street;
		private String city;

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		Adresse() {
			super();
		}

		Adresse(String street, String city) {
			super();
			this.street = street;
			this.city = city;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((city == null) ? 0 : city.hashCode());
			result = prime * result + ((street == null) ? 0 : street.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Adresse other = (Adresse) obj;
			if (city == null) {
				if (other.city != null)
					return false;
			} else if (!city.equals(other.city))
				return false;
			if (street == null) {
				if (other.street != null)
					return false;
			} else if (!street.equals(other.street))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Adresse [street=" + street + ", city=" + city + "]";
		}

	}

	public static class Person {
		private String id;
		private String name;
		private Adresse adresse;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Adresse getAdresse() {
			return adresse;
		}

		public void setAdresse(Adresse adresse) {
			this.adresse = adresse;
		}

		Person(String id, String name, Adresse adresse) {
			super();
			this.id = id;
			this.name = name;
			this.adresse = adresse;
		}

		Person() {
			super();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Person other = (Person) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Person [id=" + id + ", name=" + name + ", adresse=" + adresse + "]";
		}

	}

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

	private final static Person[] PERSON_BEAN_ARRAY;

	private static final String PERSON_BEAN_ARRAY_JSON_STRING = "[{\"id\":\"1\",\"name\":\"peter\",\"adresse\":{\"street\":\"mainstreet\",\"city\":\"smalltown\"}},{\"id\":\"1\",\"name\":\"mary\",\"adresse\":{\"street\":\"highstreet\",\"city\":\"bigcity\"}}]";

	static {
		PERSON_BEAN_ARRAY = new Person[] {
				new Person("1", "peter", new Adresse("mainstreet", "smalltown")),
				new Person("1", "mary", new Adresse("highstreet", "bigcity"))
		};
	}

	@Test
	public void testParseJson() throws JsonProcessingException, IOException {

		Map<String, Object> result = JsonUtil.parseJson(JSON_STRING);

		assertEquals("http", result.get("provider"));

	}

	@Test
	public void serializeBeans() {
		String jsonString = JsonUtil.serializeBeans(Arrays.asList(PERSON_BEAN_ARRAY));
		assertEquals(PERSON_BEAN_ARRAY_JSON_STRING, jsonString);
	}

	@Test
	public void deserializeBeans() {

		List<Person> beans = JsonUtil.deserializeBeans(new TypeReference<List<Person>>() {
		}, PERSON_BEAN_ARRAY_JSON_STRING);

		assertEquals(Arrays.asList(PERSON_BEAN_ARRAY), beans);
	}
}
