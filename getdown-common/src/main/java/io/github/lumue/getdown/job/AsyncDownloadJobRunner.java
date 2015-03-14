package io.github.lumue.getdown.job;

import io.github.lumue.getdown.resolver.ContentLocationResolverRegistry;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncDownloadJobRunner implements DownloadJobRunner {

	private final ExecutorService executorService;

	private final ContentLocationResolverRegistry contentLocationResolverRegistry;

	private final String downloadPath;



	private static Logger LOGGER = LoggerFactory.getLogger(AsyncDownloadJobRunner.class);


	public AsyncDownloadJobRunner(ExecutorService executorService, ContentLocationResolverRegistry contentLocationResolverRegistry,
			String downloadPath) {
		super();
		this.executorService = executorService;
		this.contentLocationResolverRegistry = contentLocationResolverRegistry;
		this.downloadPath = downloadPath;
	}


	@Override
	public void runJob(final DownloadJob job) {
		this.executorService.execute(new Runnable() {

			@Override
			public void run() {
				AsyncDownloadJobRunner.LOGGER.debug("starting download for url " + job.getUrl());
				job.run(downloadPath, contentLocationResolverRegistry);

			}


		});

	}

}
