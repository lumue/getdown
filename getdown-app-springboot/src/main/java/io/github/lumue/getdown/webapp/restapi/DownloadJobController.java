package io.github.lumue.getdown.webapp.restapi;


import io.github.lumue.getdown.core.download.DownloadService;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.bus.Event;
import reactor.fn.Consumer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Webapi Rest Controller for DownloadJobs
 *
 * @author lm
 */

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin
public class DownloadJobController implements Consumer<Event<DownloadJob>> {
	
	
	private final DownloadService downloadService;
	
	private final SseEmitters sseEmitters=new SseEmitters();

	
	@Autowired
	public DownloadJobController(DownloadService downloadService) {
		
		this.downloadService = downloadService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	CollectionModel<EntityModel<DownloadJob>> getAll() {
		return CollectionModel.wrap(
				this.downloadService.streamDownloads()
						.collect(Collectors.toList())
		);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	CollectionModel<EntityModel<DownloadJob>> post(@RequestBody List<DownloadTask> tasks) {
		return CollectionModel.wrap(
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
