package io.github.lumue.getdown.hosts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import io.github.lumue.getdown.core.download.resolver.ContentLocation;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolver;

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

public abstract class AbstractUrlResolverTest {

	private static final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();



	private static final HttpRequestFactory REQUEST_FACTORY = HTTP_TRANSPORT.createRequestFactory();

	public AbstractUrlResolverTest() {
		super();
	}

	@Before
	public void init()
	{
		CookieHandler.setDefault(new CookieManager());
	}
	
	@Test
	public void testResolve() throws IOException {
	
		ContentLocationResolver scraper = newUrlResolver();
		ContentLocation contentLocation = scraper.resolve(getStartUrl());
		String location = contentLocation.getUrl();
		
		assertNotNull("scraper returned null", location);
		
		assertEquals("wrong filename",getExpectedFilename() ,contentLocation.getFilename());
	
		assertTrue("scraped url expected to end with "+getExpectedTargetSuffix(), location.endsWith(getExpectedTargetSuffix()));
	
		long size = getContentSize(location);
		assertEquals("content at scraped url has wrong size", getExpectedContentSize(), size);
	}
	

	private long getContentSize(String result) throws IOException {
		HttpRequest request = REQUEST_FACTORY.buildGetRequest(new GenericUrl(result));
		HttpResponse response = request.execute();
		long size = response.getHeaders().getContentLength();
		return size;
	}

	public abstract ContentLocationResolver newUrlResolver();

	public abstract String getExpectedFilename();

	public abstract String getStartUrl();

	public abstract String getExpectedTargetSuffix();

	public abstract long getExpectedContentSize();



}