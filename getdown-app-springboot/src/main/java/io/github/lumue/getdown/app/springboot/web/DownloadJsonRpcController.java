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

	/**
	 * Add and start a download
	 * @param url
	 * @return
	 */
	@RequestMapping( method = RequestMethod.POST)
	public DownloadJobView postDownload(@RequestBody String url) {
		return this.addDownload(url);
	}

	/**
	 * get all downloads
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listDownloads() {

		return downloadService
				.streamDownloads()
				.map(DownloadJobView::wrap)
				.collect(Collectors.toList());
	}

	/**
	 * get finished downloads
	 * @return
	 */
	@RequestMapping(value = "/state/finished", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listFinishedDownloads() {
		return downloadService
				.streamFinishedDownloads()
				.map(DownloadJobView::wrap)
				.collect(Collectors.toList());
	}

	/**
	 * get waiting
	 * @return
	 */
	@RequestMapping(value = "/state/waiting", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listWaitingDownloads() {
		return downloadService
				.streamWaitingDownloads()
				.map(DownloadJobView::wrap)
				.collect(Collectors.toList());
	}

	/**
	 * get running downloads
	 * @return
	 */
	@RequestMapping(value = "/state/running", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listRunningDownloads() {
		return downloadService
				.streamRunningDownloads()
				.map(DownloadJobView::wrap)
				.collect(Collectors.toList());
	}

	/**
	 * get failed downloads
	 * @return
	 */
	@RequestMapping(value = "/state/error", method = RequestMethod.GET)
	public Iterable<DownloadJobView> listFailedDownloads() {
		return downloadService
				.streamFailedDownloads()
				.map(DownloadJobView::wrap)
				.collect(Collectors.toList());
	}


	
	@RequestMapping(value = "/{handle}", method = RequestMethod.GET)
	public DownloadJobView getDownload(@PathVariable String handle) {
		DownloadJob downloadJob=downloadService.getDownload(new Download.DownloadJobHandle(handle));
		return DownloadJobView.wrap(downloadJob);
	}

	@RequestMapping(value = "/{handle}", method = RequestMethod.DELETE)
	public void removeDownload(@PathVariable String handle) {
		downloadService.removeDownload(new Download.DownloadJobHandle(handle));
	}
	
	@RequestMapping(value = "/{handle}/cancel", method = RequestMethod.POST)
	public void cancelDownload(@PathVariable String handle) {
		LOGGER.debug("canceling download job with handle " + handle);
		downloadService.cancelDownload(new Download.DownloadJobHandle(handle));
	}

	@RequestMapping(value = "/{handle}/restart", method = RequestMethod.POST)
	public void restartDownload(@PathVariable String handle) {
		LOGGER.debug("canceling download job with handle " + handle);
		downloadService.restartDownload(new Download.DownloadJobHandle(handle));
	}
	



}
