package io.github.lumue.getdown.webapp.websocket;

import io.github.lumue.getdown.core.download.task.DownloadTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import reactor.bus.Event;

import java.util.function.Consumer;

@Controller
public class TaskWebsocketController implements Consumer<Event<DownloadTask>> {



	private final SimpMessagingTemplate template;

	@Autowired
	public TaskWebsocketController(SimpMessagingTemplate template) {
		super();
		this.template = template;
	}


	@Override
	public void accept(Event<DownloadTask> downloadTaskEvent) {
		String topic=downloadTaskEvent.getHeaders().get("group1");
		template.convertAndSend("/tasks/"+topic, downloadTaskEvent.getData());
	}
}