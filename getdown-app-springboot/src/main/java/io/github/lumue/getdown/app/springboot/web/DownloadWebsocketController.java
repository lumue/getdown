package io.github.lumue.getdown.app.springboot.web;

import static reactor.event.selector.Selectors.$;
import io.github.lumue.getdown.core.download.job.DownloadJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import reactor.core.Reactor;
import reactor.event.Event;

@Controller
public class DownloadWebsocketController {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	public DownloadWebsocketController(final Reactor reactor) {
		super();
		reactor.on($("downloads"), this::broadcastJobStateChange);
	}

	public void broadcastJobStateChange(Event<DownloadJob> event) {
		template.convertAndSend("/downloads/jobStateChange", DownloadJobView.wrap(event.getData()));
	}
}
