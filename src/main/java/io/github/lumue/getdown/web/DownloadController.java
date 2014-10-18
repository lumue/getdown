package io.github.lumue.getdown.web;


import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.lumue.getdown.application.DownloadJob;
import io.github.lumue.getdown.application.DownloadJob.DownloadJobHandle;
import io.github.lumue.getdown.application.DownloadService;
import io.github.lumue.getdown.util.CollectionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownloadController {

	@Autowired
	DownloadService downloadService;

	@RequestMapping("/download/add")
	public String addDownload(@RequestParam(value = "url", required = true) String url) {
		return downloadService.addDownload(url).toString();
	}

	@RequestMapping("/download/start")
	public void startDownload(@RequestParam(value = "handle", required = true) String handle) {
		downloadService.startDownload(DownloadJobHandle.create(handle));
	}

	@RequestMapping("/download/list" )
	public Iterable<DownloadViewItem> listDownloads() {
		Stream<DownloadJob> stream = CollectionUtil.stream(downloadService.listDownloads());
		return stream.map(downloadJob -> DownloadViewItem.wrap(downloadJob)).collect(Collectors.toList());
	}
	


}
