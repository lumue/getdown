package io.github.lumue.getdown.web;


import io.github.lumue.getdown.application.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.application.DownloadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownloadController {

	@Autowired
	DownloadService downloadService;

	@RequestMapping("/download/add")
	public DownloadJobHandle addDownload(@RequestParam(value = "url", required = true) String url) {
		return downloadService.addDownload(url);
	}

	@RequestMapping("/download/start")
	public void startDownload(@RequestParam(value = "handle", required = true) String handle) {
		downloadService.startDownload(DownloadJobHandle.create(handle));
	}

	


}
