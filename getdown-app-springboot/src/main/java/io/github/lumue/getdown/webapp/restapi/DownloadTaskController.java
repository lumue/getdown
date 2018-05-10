package io.github.lumue.getdown.webapp.restapi;


import io.github.lumue.getdown.core.download.DownloadService;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import io.github.lumue.getdown.core.download.task.DownloadTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Webapi Rest Controller for DownloadTask
 *
 * @author lm
 * @created 08.12.16.
 */

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin
public class DownloadTaskController implements Consumer<Event<DownloadTask>> {
	
	private final static Logger LOGGER=LoggerFactory.getLogger(DownloadTaskController.class);
	
	
	private final DownloadTaskRepository taskRepository;

	private final DownloadService downloadService;
	
	private final List<SseEmitter> emitters = new ArrayList<>();
	
	
	@Autowired
	public DownloadTaskController(DownloadTaskRepository taskRepository, DownloadService downloadService) {
		this.taskRepository = taskRepository;
		this.downloadService = downloadService;
	}

	@RequestMapping(method = RequestMethod.GET)
	Resources<Resource<DownloadTask>> getAll() {
		return Resources.wrap(this.taskRepository.list());
	}

	@RequestMapping(method = RequestMethod.POST)
	Resources<Resource<DownloadTask>> post(@RequestBody List<String> urls) {
		return Resources.wrap(
				urls.stream()
				.map(downloadService::addDownloadTask)
				.collect(Collectors.toList())
		);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	Resources<Resource<DownloadTask>> delete(@RequestBody List<DownloadTask> tasks) {
		return Resources.wrap(
				tasks.stream()
						.map(downloadService::removeDownloadTask)
						.collect(Collectors.toList())
		);
	}

	@RequestMapping(value="/{key}", method = RequestMethod.GET)
	ResponseEntity<Resource<DownloadTask>> getOne(@PathVariable("key") String id){
		DownloadTask downloadTask = this.taskRepository.get(id);
		if(downloadTask==null)
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Resource<DownloadTask> downloadTaskResource = new Resource<>(downloadTask);
		return ResponseEntity.ok(downloadTaskResource);

	}
	@RequestMapping(path = "/events", method = RequestMethod.GET)
	public SseEmitter stream() {
		
		SseEmitter emitter = new SseEmitter();
		
		this.emitters.add(emitter);
		emitter.onCompletion(() -> emitters.remove(emitter));
		
		return emitter;
	}
	
	@Override
	public void accept(Event<DownloadTask> downloadJobEvent) {
		Message<DownloadTask> message=MessageBuilder.withPayload(downloadJobEvent.getData()).build();
		emitters.forEach(sseEmitter -> {
			try {
				sseEmitter.send(message);
			} catch (Throwable e) {
				sseEmitter.complete();
				LOGGER.error("Error sending Message to "+sseEmitter,e);
			}
		});
	}

}
