package io.github.lumue.getdown;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeRenderer {

	/**
	 * 
	 * @param timeMillis
	 * @return
	 */
	public String render(long timeMillis) {
		ZonedDateTime instant = Instant.ofEpochMilli(timeMillis).atZone(Clock.systemDefaultZone().getZone());
		LocalDate localDate=LocalDate.from(instant);
		return localDate.format(DateTimeFormatter.ISO_DATE);
	}

}
