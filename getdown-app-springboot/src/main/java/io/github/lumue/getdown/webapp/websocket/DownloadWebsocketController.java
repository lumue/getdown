package io.github.lumue.getdown.webapp.websocket;

import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.webapp.webapi.DownloadJobView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import reactor.bus.Event;

import java.util.function.Consumer;

@Controller
public class DownloadWebsocketController implements Consumer<Event<DownloadJob>> {



	private final SimpMessagingTemplate template;

	@Autowired
	public DownloadWebsocketController(SimpMessagingTemplate template) {
		super();
		this.template = template;
	}


	@Override
	public void accept(Event<DownloadJob> downloadJobEvent) {
		template.convertAndSend("/downloads/jobStateChange", DownloadJobView.wrap(downloadJobEvent.getData()));
	}
}
