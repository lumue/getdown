package io.github.lumue.getdown.scraper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.github.lumue.getdown.scraper.StreamcloudUrlScraper;
import io.github.lumue.getdown.scraper.UrlScraper;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;

import org.junit.Before;
import org.junit.Test;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

public abstract class AbstractUrlScraperTest {

	private static final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();

	public abstract String getStartUrl();

	public abstract String getExpectedTargetSuffix();

	public abstract long getExpectedContentSize();

	private static final HttpRequestFactory REQUEST_FACTORY = HTTP_TRANSPORT.createRequestFactory();

	public AbstractUrlScraperTest() {
		super();
	}

	@Before
	public void init()
	{
		CookieHandler.setDefault(new CookieManager());
	}

	@Test
	public void testScrape() throws IOException {
	
		UrlScraper scraper = new StreamcloudUrlScraper();
		String result = scraper.scrape(getStartUrl());
		
		assertNotNull("scraper returned null", result);
	
		assertTrue("scraped url expected to end with "+getExpectedTargetSuffix(), result.endsWith(getExpectedTargetSuffix()));
	
		long size = getContentSize(result);
		assertEquals("content at scraped url has wrong size", getExpectedContentSize(), size);
	}

	private long getContentSize(String result) throws IOException {
		HttpRequest request = REQUEST_FACTORY.buildGetRequest(new GenericUrl(result));
		HttpResponse response = request.execute();
		long size = response.getHeaders().getContentLength();
		return size;
	}

}