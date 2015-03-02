package io.github.lumue.getdown.hosts.streamcloud;

import io.github.lumue.getdown.hosts.AbstractUrlResolverTest;
import io.github.lumue.getdown.resolver.ContentLocationResolver;

public class StreamcloudUrlResolverTest extends AbstractUrlResolverTest {

	private static final long EXPECTED_CONTENT_SIZE = 73240803L;

	private static final String EXPECTED_TARGET_URL_SUFFIX = "video.mp4";

	private static final String START_URL = "http://streamcloud.eu/w7l5g5enduu9/The.Big.Bang.Theory.S08E10.720p.HDTV.X264-DIMENSION.mkv.html";

	private static final String EXPECTED_FILENAME = "The.Big.Bang.Theory.S08E10.720p.HDTV.X264-DIMENSION.mkv";

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

}
