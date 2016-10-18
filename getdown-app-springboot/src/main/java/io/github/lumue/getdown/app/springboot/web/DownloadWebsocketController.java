package io.github.lumue.getdown.app.springboot.web;

import static reactor.bus.selector.Selectors.$;
import io.github.lumue.getdown.core.download.job.DownloadJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import reactor.bus.Event;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;

@Controller
public class DownloadWebsocketController {

	private final EventBus eventbus;

	private final SimpMessagingTemplate template;

	@Autowired
	public DownloadWebsocketController(final EventBus eventbus, SimpMessagingTemplate template) {
		super();
		this.eventbus=eventbus;
		this.template = template;
	}

	@PostConstruct
	public void startSubcription(){
		eventbus.on($("throtteled-downloads"),
				this::broadcastJobStateChange);
	}

	private void broadcastJobStateChange(Event<DownloadJob> event) {
		template.convertAndSend("/downloads/jobStateChange", DownloadJobView.wrap(event.getData()));
	}
}
