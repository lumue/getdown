package io.github.lumue.getdown.core.download.job;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.bus.Event;
import reactor.bus.EventBus;

public class AsyncDownloadJobRunner {

	private final ExecutorService executorService;

	private final ContentLocationResolverRegistry contentLocationResolverRegistry;

	private final String downloadPath;

	private final EventBus eventbus;


	private static Logger LOGGER = LoggerFactory.getLogger(AsyncDownloadJobRunner.class);


	public AsyncDownloadJobRunner(ExecutorService executorService, ContentLocationResolverRegistry contentLocationResolverRegistry,
			String downloadPath, EventBus eventbus) {
		super();
		this.executorService = executorService;
		this.contentLocationResolverRegistry = contentLocationResolverRegistry;
		this.downloadPath = downloadPath;
		this.eventbus = eventbus;
	}



	public void runJob(final DownloadJob job) {
		job.addObserver( o ->
			{
				eventbus.notify("downloads", Event.wrap(o));
			});
		job.setDownloadPath(this.downloadPath);
		job.setContentLocationResolverRegistry(this.contentLocationResolverRegistry);
		AsyncDownloadJobRunner.LOGGER.debug("starting download for url " + job.getUrl());
		this.executorService.submit(job);
	}



	public void cancelJob(DownloadJob job) {
		job.cancel();
	}

}
