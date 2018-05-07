package io.github.lumue.getdown.webapp.restapi;


import io.github.lumue.getdown.core.download.DownloadService;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Webapi Rest Controller for DownloadJobs
 *
 *
 * @author lm
 * @created 08.12.16.
 */

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin
public class DownloadJobController {


	private final DownloadService downloadService;

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




}
