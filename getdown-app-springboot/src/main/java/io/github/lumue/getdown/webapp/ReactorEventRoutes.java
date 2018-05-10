package io.github.lumue.getdown.webapp;

import io.github.lumue.getdown.webapp.restapi.DownloadJobController;
import io.github.lumue.getdown.webapp.restapi.DownloadTaskController;
import io.github.lumue.getdown.webapp.websocket.DownloadWebsocketController;
import io.github.lumue.getdown.core.download.job.ThrottlingDownloadJobEventTap;
import io.github.lumue.getdown.webapp.websocket.TaskWebsocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;

import static reactor.bus.selector.Selectors.$;
import static reactor.bus.selector.Selectors.R;

/**
 * setup event routing
 *
 * Created by lm on 19.10.16.
 */
@Component
@Lazy(false)
public class ReactorEventRoutes {

	private static final String THROTTELED_DOWNLOADS = "throtteled-downloads";

	private final DownloadWebsocketController downloadWebsocketController;

	private final TaskWebsocketController taskWebsocketController;

	private final EventBus eventbus;

	private final ThrottlingDownloadJobEventTap throttlingDownloadJobEventTap;

	private final DownloadJobController downloadJobController;
	
	private final DownloadTaskController downloadTaskController;
	
	@Autowired
	public ReactorEventRoutes(DownloadWebsocketController websocketController,
	                          TaskWebsocketController taskWebsocketController,
	                          EventBus eventbus,
	                          DownloadJobController downloadJobController, DownloadTaskController downloadTaskController) {
		this.downloadWebsocketController = websocketController;
		this.taskWebsocketController = taskWebsocketController;
		this.eventbus = eventbus;
		this.throttlingDownloadJobEventTap = new ThrottlingDownloadJobEventTap(eventbus, THROTTELED_DOWNLOADS, 5000);
		this.downloadJobController = downloadJobController;
		this.downloadTaskController = downloadTaskController;
	}

	/**
	 * sets up subscriptions for reactor events
	 */
	@PostConstruct
	public void setup(){
		this.eventbus.on(R("downloads-(.+)"),throttlingDownloadJobEventTap);
		this.eventbus.on($(THROTTELED_DOWNLOADS), downloadWebsocketController);
		this.eventbus.on($(THROTTELED_DOWNLOADS), downloadJobController);
		this.eventbus.on(R("tasks-(.+)"), downloadWebsocketController);
		this.eventbus.on(R("tasks-(.+)"), downloadTaskController);
	}
}
