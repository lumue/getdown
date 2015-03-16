package io.github.lumue.getdown.core.download.job;

import io.github.lumue.getdown.core.download.resolver.ContentLocationResolverRegistry;

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
		this.executorService.execute(new Runnable() {

			@Override
			public void run() {
				AsyncDownloadJobRunner.LOGGER.debug("starting download for url " + job.getUrl());
				job.run(downloadPath, contentLocationResolverRegistry, progress -> {
					eventbus.notify("downloads", Event.wrap(job));
				});

			}


		});

	}

}
