package io.github.lumue.getdown.webapp.restapi;


import io.github.lumue.getdown.core.download.DownloadService;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import io.github.lumue.getdown.core.download.task.DownloadTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Webapi Rest Controller for DownloadTask
 *
 * @author lm
 * @created 08.12.16.
 */

@RestController
@RequestMapping("/api/tasks")
public class DownloadTaskController{

	private final DownloadTaskRepository taskRepository;

	private final DownloadService downloadService;

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
	Resources<Resource<DownloadTask>> post(@RequestBody Resources<Resource<DownloadTask>> downloadTasks) {
		return Resources.wrap(
				downloadTasks.getContent().stream()
				.map(u->downloadService.addDownload(u.getContent()))
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


}
