package io.github.lumue.getdown.app.springboot.web;

import static reactor.event.selector.Selectors.$;
import io.github.lumue.getdown.core.download.job.DownloadJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import reactor.core.Reactor;
import reactor.event.Event;

@Controller
public class DownloadWebsocketController {

	@Autowired
	public DownloadWebsocketController(final Reactor reactor) {
		super();
		reactor.on($("downloads"), this::broadcastJobStateChange);
	}

	@MessageMapping("/downloads")
	public DownloadJobView broadcastJobStateChange(Event<DownloadJob> event) {
		return DownloadJobView.wrap(event.getData());
	}
}
