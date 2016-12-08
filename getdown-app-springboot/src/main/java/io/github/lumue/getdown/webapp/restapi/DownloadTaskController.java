package io.github.lumue.getdown.webapp.restapi;


import io.github.lumue.getdown.core.download.DownloadService;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import io.github.lumue.getdown.core.download.task.DownloadTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
	Resources<Resource<DownloadTask>> post(@PathVariable("url") List<String> urls) {
		return Resources.wrap(urls.stream()
				.map(u->downloadService.addDownload(u))
				.collect(Collectors.toList())
		);
	}

	@RequestMapping(value="/{key}", method = RequestMethod.GET)
	Resource<DownloadTask> getOne(@PathVariable("key") String id){
		return new Resource<>(this.taskRepository.get(id));
	}

}
