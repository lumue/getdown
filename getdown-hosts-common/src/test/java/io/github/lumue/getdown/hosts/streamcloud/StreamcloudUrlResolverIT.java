package io.github.lumue.getdown.hosts.streamcloud;

import java.io.IOException;

import org.junit.Test;

import io.github.lumue.getdown.core.download.resolver.ContentLocationResolver;
import io.github.lumue.getdown.hosts.AbstractUrlResolverTest;

/**
 * Integration test connecting to an actual streamcloud url
 *
 * @author lm
 *
 */
public class StreamcloudUrlResolverIT extends AbstractUrlResolverTest {

	private static final long EXPECTED_CONTENT_SIZE = 456183886L;

	private static final String EXPECTED_TARGET_URL_SUFFIX = "video.mp4";

	private static final String START_URL = "http://streamcloud.eu/jot4frw4qfm5/S02E03_-_Wer_mit_dem_Feuer_spielt.avi.html";

	private static final String EXPECTED_FILENAME = "S02E03_-_Wer_mit_dem_Feuer_spielt.avi";

	@Override
	public long getExpectedContentSize() {
		return EXPECTED_CONTENT_SIZE;
	}

	@Override
	public String getExpectedTargetSuffix() {
		return EXPECTED_TARGET_URL_SUFFIX;
	}

	@Override
	public String getStartUrl() {
		return START_URL;
	}

	@Override
	public String getExpectedFilename() {
		return EXPECTED_FILENAME;
	}

	@Override
	public ContentLocationResolver newUrlResolver() {
		return new StreamcloudContentLocationResolver();
	}

	/**
	 * test for an error occuring when trying to resolve two streamcloud
	 * locations after another
	 *
	 * @throws IOException
	 */
	@Test
	public void testBackToBackResolve() throws IOException {
		testResolve();
		testResolve();
	}
}
