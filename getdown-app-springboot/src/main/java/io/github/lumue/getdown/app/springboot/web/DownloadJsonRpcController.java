package io.github.lumue.getdown.app.springboot.web;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import io.github.lumue.getdown.core.common.util.StreamUtils;
import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadService;

@RestController
@CrossOrigin(origins = "*")
public class DownloadJsonRpcController {

	private final DownloadService downloadService;


	@Autowired
	public DownloadJsonRpcController(DownloadService downloadService, SimpMessagingTemplate messagingTemplate) {
		super();
		this.downloadService = downloadService;
	}

	private final static Logger LOGGER = LoggerFactory.getLogger(DownloadJsonRpcController.class);

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
	public DownloadJobView getDownload(@PathVariable String handle) {
		DownloadJob downloadJob=downloadService.getDownload(new DownloadJob.DownloadJobHandle(handle));
		return DownloadJobView.wrap(downloadJob);
	}
	
	@RequestMapping(value = "/download/{handle}/cancel", method = RequestMethod.GET)
	public void cancelDownload(@PathVariable String handle) {
		LOGGER.debug("canceling download job with handle " + handle);
		downloadService.cancelDownload(new DownloadJob.DownloadJobHandle(handle));
	}
	



}
