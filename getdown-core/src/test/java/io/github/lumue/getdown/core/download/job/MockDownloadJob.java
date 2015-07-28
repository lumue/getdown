package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.download.job.DownloadJob.AbstractDownloadJob;
import io.github.lumue.getdown.core.download.resolver.ContentLocationResolverRegistry;
/**
 * A DownloadJob for testing
 * @author lm
 *
 */
@SuppressWarnings("serial")
public class MockDownloadJob extends AbstractDownloadJob{

	public MockDownloadJob() {
		super("", "");
	}

	@Override
	public void run(String downloadPath, ContentLocationResolverRegistry contentLocationResolverRegistry,
			DownloadJobProgressListener progressListener) {
	} 
	
	/**
	 * simulate starting
	 */
	public void doStart(){
		start(null);
	}
	
	/**
	 * simulate finishing
	 */
	public void doFinish(){
		finish(null);
	}
}