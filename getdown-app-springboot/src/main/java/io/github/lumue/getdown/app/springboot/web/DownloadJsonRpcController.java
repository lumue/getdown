package io.github.lumue.getdown.app.springboot.web;

import java.util.stream.Collectors;

import io.github.lumue.getdown.core.download.job.Download;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import io.github.lumue.getdown.core.download.job.DownloadJob;
import io.github.lumue.getdown.core.download.job.DownloadService;

@RestController
@RequestMapping("/download")
@CrossOrigin
public class DownloadJsonRpcController {

	private final DownloadService downloadService;


	@Autowired
	public DownloadJsonRpcController(DownloadService downloadService, SimpMessagingTemplate messagingTemplate) {
		super();
		this.downloadService = downloadService;
	}

	private final static Logger LOGGER = LoggerFactory.getLogger(DownloadJsonRpcController.class);

	/**
	 * Add and start a download
	 * @param url
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.PUT)
	public DownloadJobView addDownload(@RequestParam(value = "url", required = true) String url) {

		LOGGER.debug("adding and starting download job for " + url);

		DownloadJob download = downloadService.addDownload(url);
		downloadService.startDownload(download.getHandle());

		LOGGER.debug("download job for " + url + " added and started");

		return DownloadJobView.wrap(download);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listDownloads() {

		return downloadService
				.streamDownloads()
				.map(downloadJob -> DownloadJobView.wrap(downloadJob))
				.collect(Collectors.toList());
	}

	/**
	 * Remove finished and failed downloads
	 * @return
	 */
	@RequestMapping(value = "/state/finished", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listFinishedDownloads() {
		return downloadService
				.streamFinishedDownloads()
				.map(downloadJob -> DownloadJobView.wrap(downloadJob))
				.collect(Collectors.toList());
	}

	/**
	 * Remove finished and failed downloads
	 * @return
	 */
	@RequestMapping(value = "/state/waiting", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listWaitingDownloads() {
		return downloadService
				.streamWaitingDownloads()
				.map(downloadJob -> DownloadJobView.wrap(downloadJob))
				.collect(Collectors.toList());
	}

	/**
	 * Remove finished and failed downloads
	 * @return
	 */
	@RequestMapping(value = "/state/running", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listRunningDownloads() {
		return downloadService
				.streamRunningDownloads()
				.map(downloadJob -> DownloadJobView.wrap(downloadJob))
				.collect(Collectors.toList());
	}

	/**
	 * Remove finished and failed downloads
	 * @return
	 */
	@RequestMapping(value = "/state/error", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listFailedDownloads() {
		return downloadService
				.streamFailedDownloads()
				.map(downloadJob -> DownloadJobView.wrap(downloadJob))
				.collect(Collectors.toList());
	}


	
	@RequestMapping(value = "/{handle}", method = RequestMethod.GET)
	public DownloadJobView getDownload(@PathVariable String handle) {
		DownloadJob downloadJob=downloadService.getDownload(new Download.DownloadJobHandle(handle));
		return DownloadJobView.wrap(downloadJob);
	}
	
	@RequestMapping(value = "/{handle}/cancel", method = RequestMethod.GET)
	public void cancelDownload(@PathVariable String handle) {
		LOGGER.debug("canceling download job with handle " + handle);
		downloadService.cancelDownload(new Download.DownloadJobHandle(handle));
	}
	



}
