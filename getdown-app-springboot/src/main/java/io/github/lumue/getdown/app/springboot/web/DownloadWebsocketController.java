package io.github.lumue.getdown.app.springboot.web;

import static reactor.bus.selector.Selectors.$;
import io.github.lumue.getdown.core.download.job.DownloadJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import reactor.bus.Event;
import reactor.bus.EventBus;

@Controller
public class DownloadWebsocketController {

	@Autowired
	private SimpMessagingTemplate template;

	@Autowired
	public DownloadWebsocketController(final EventBus eventbus) {
		super();
		eventbus.on($("ws-downloads"), this::broadcastJobStateChange);
	}

	public void broadcastJobStateChange(Event<DownloadJob> event) {
		template.convertAndSend("/downloads/jobStateChange", DownloadJobView.wrap(event.getData()));
	}
}
