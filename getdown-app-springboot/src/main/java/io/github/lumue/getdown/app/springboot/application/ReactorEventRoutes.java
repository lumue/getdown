package io.github.lumue.getdown.app.springboot.application;

import io.github.lumue.getdown.app.springboot.web.DownloadWebsocketController;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadJobRepository;
import io.github.lumue.getdown.core.download.job.ThrottelingDownloadJobEventTap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.bus.Event;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;

import static reactor.bus.selector.Selectors.$;

/**
 * setup event routing
 *
 * Created by lm on 19.10.16.
 */
@Component
@Lazy(false)
public class ReactorEventRoutes {

	private final DownloadWebsocketController websocketController;

	private final DownloadJobRepository downloadJobRepository;

	private final EventBus eventbus;

	private final ThrottelingDownloadJobEventTap throttelingDownloadJobEventTap;

	@Autowired
	public ReactorEventRoutes(DownloadWebsocketController websocketController, DownloadJobRepository downloadJobRepository, EventBus eventbus, ThrottelingDownloadJobEventTap throttelingDownloadJobEventTap) {
		this.websocketController = websocketController;
		this.downloadJobRepository = downloadJobRepository;
		this.eventbus = eventbus;
		this.throttelingDownloadJobEventTap = throttelingDownloadJobEventTap;
	}

	/**
	 * sets up subscriptions for reactor events
	 */
	@PostConstruct
	public void setup(){
		this.eventbus.on($("downloads"), this.throttelingDownloadJobEventTap);
		this.eventbus.on($("throtteled-downloads"), (Event<DownloadJob> e) -> this.downloadJobRepository.update(e.getData()));
		this.eventbus.on($("throtteled-downloads"),this.websocketController);
	}
}
