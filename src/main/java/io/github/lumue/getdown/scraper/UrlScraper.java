package io.github.lumue.getdown.scraper;

import java.io.IOException;

public interface UrlScraper {
	public String scrape(String url) throws IOException;
}
