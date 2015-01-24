package io.github.lumue.getdown.web;


import io.github.lumue.getdown.application.DownloadJob;
import io.github.lumue.getdown.application.DownloadService;
import io.github.lumue.getdown.util.StreamUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownloadController {

	@Autowired
	DownloadService downloadService;

	private final static Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

	@RequestMapping(value = "/download/add", method = RequestMethod.PUT)
	public DownloadJobView addDownload(@RequestParam(value = "url", required = true) String url) {
		LOGGER.debug("adding and starting download job for " + url);
		DownloadJob download = downloadService.addDownload(url);
		downloadService.startDownload(download.getHandle());
		LOGGER.debug("download job for " + url + " added and started");
		return DownloadJobView.wrap(download);
	}
	
	@RequestMapping(value = "/download/list", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listDownloads() {
		Stream<DownloadJob> stream = StreamUtils.stream(downloadService.listDownloads());
		return stream.map(downloadJob -> DownloadJobView.wrap(downloadJob)).collect(Collectors.toList());
	}
	
	@RequestMapping(value = "/download/{handle}", method = RequestMethod.GET)
	public Iterable<DownloadJobView> getDownload(@PathVariable String handle) {
		Stream<DownloadJob> stream = StreamUtils.stream(downloadService.listDownloads());
		return stream.map(downloadJob -> DownloadJobView.wrap(downloadJob)).collect(Collectors.toList());
	}

}
