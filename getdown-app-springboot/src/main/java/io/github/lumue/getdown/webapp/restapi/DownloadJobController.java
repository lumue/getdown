package io.github.lumue.getdown.webapp.restapi;


import io.github.lumue.getdown.core.download.DownloadService;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.bus.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Webapi Rest Controller for DownloadJobs
 *
 * @author lm
 * @created 08.12.16.
 */

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin
public class DownloadJobController implements Consumer<Event<DownloadJob>> {
	
	
	private final DownloadService downloadService;
	
	private final SseEmitters sseEmitters=new SseEmitters();
	
	private final static Logger LOGGER=LoggerFactory.getLogger(DownloadJobController.class);
	
	@Autowired
	public DownloadJobController(DownloadService downloadService) {
		
		this.downloadService = downloadService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	Resources<Resource<DownloadJob>> getAll() {
		return Resources.wrap(
				this.downloadService.streamDownloads()
						.collect(Collectors.toList())
		);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	Resources<Resource<DownloadJob>> post(@RequestBody List<DownloadTask> tasks) {
		return Resources.wrap(
				tasks.stream()
						.map(DownloadTask::getHandle)
						.map(downloadService::startDownload)
						.collect(Collectors.toList())
		);
	}
	
	@RequestMapping(path = "/events", method = RequestMethod.GET)
	public SseEmitter stream() {
		return sseEmitters.newEmitter();
	}
	
	@Override
	public void accept(Event<DownloadJob> downloadJobEvent) {
		Message<DownloadJob> message=MessageBuilder.withPayload(downloadJobEvent.getData()).build();
		sseEmitters.sendMessage(message);
	}
	
}
