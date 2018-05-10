package io.github.lumue.getdown.core.download.downloader.youtubedl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.lumue.getdown.core.common.util.Observable;
import io.github.lumue.getdown.core.common.util.ObservableTemplate;
import io.github.lumue.getdown.core.common.util.Observer;
import io.github.lumue.getdown.core.download.task.DownloadFormat;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import io.github.lumue.getdown.core.download.task.ValidateTaskJob;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.metadata.single_info_json.Format;
import io.github.lumue.ydlwrapper.metadata.single_info_json.RequestedFormat;
import io.github.lumue.ydlwrapper.metadata.single_info_json.SingleInfoJsonMetadataAccessor;
import io.github.lumue.ydlwrapper.metadata.statusmessage.YdlStatusMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class YoutubedlValidateTaskJob extends ValidateTaskJob  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YoutubedlValidateTaskJob.class);
	private transient AtomicReference<YdlDownloadTask> ydlTaskReference = new AtomicReference<>(null);
	private boolean forceMp4OnYoutube = true;
	
	private String pathToYdl = "/usr/bin/youtube-dl";
	
	@JsonIgnore
	private  ObservableTemplate observableTemplate=new ObservableTemplate(this);
	
	
	public YoutubedlValidateTaskJob(DownloadTask task) {
		super(task);
	}
	
	@Override
	public ValidateTaskJob removeObservers() {
		observableTemplate=new ObservableTemplate(this);
		return this;
	}
	
	@Override
	public void run() {
		try {
			setValidatingState();
			getYdlTask().prepare();
			getYdlTask().getYdlDownloadTaskMetadata().ifPresent(
					ydlInfoJson -> {
						getTask().setName(ydlInfoJson.getTitle());
						final String formatId = ydlInfoJson.getFormatId();
						final List<DownloadFormat> availableFormats = toFormats(ydlInfoJson.getFormats());
						getTask().setAvailableFormats(availableFormats);
						boolean isMergedFormats = ydlInfoJson.getRequestedFormats() != null && ydlInfoJson.getRequestedFormats().size() > 1;
						final List<DownloadFormat> selectedFormats = isMergedFormats ?
								toFormatsFromRequested(ydlInfoJson.getRequestedFormats()):
								availableFormats.stream().filter(
										f -> f.getFormatId().equals(formatId)
								).collect(Collectors.toList());
						getTask().setSelectedFormats(selectedFormats);
						getTask().setExpectedSize(selectedFormats.stream().mapToLong(f->f.getExpectedSize()).sum());
					}
			);
			
			setValidatedState();
		} catch (Exception e) {
			setInvalidState();
			LOGGER.error("error validating task ", e);
		}
	}
	
	private void setInvalidState() {
		observableTemplate.doObserved(()->
				getTask().invalid()
		);
	}
	
	private void setValidatedState() {
		observableTemplate.doObserved(()->
		getTask().validated()
		);
	}
	
	private void setValidatingState() {
		observableTemplate.doObserved(()->
			getTask().validating()
		);
	}
	
	private List<DownloadFormat> toFormatsFromRequested(List<RequestedFormat> requestedFormats) {
		return requestedFormats.stream()
				.map(YoutubedlValidateTaskJob::toFormat)
				.collect(Collectors.toList());
	}
	
	private List<DownloadFormat> toFormats(List<Format> formats) {
		return formats.stream()
				.map(YoutubedlValidateTaskJob::toFormat)
				.collect(Collectors.toList());
	}
	
	private static DownloadFormat toFormat(Format format) {
		String type = "unknown";
		if (!StringUtils.isEmpty(format.getContainer()))
			type = format.getContainer();
		else if (!StringUtils.isEmpty(format.getVcodec()))
			type = format.getVcodec();
		else if (!StringUtils.isEmpty(format.getAcodec()))
			type = format.getAcodec();
		
		return new DownloadFormat(type,
				format.getFormatId(),
				format.getFilesize() != null ? format.getFilesize().longValue() : getFilesizeFromUrl(format.getUrl()),
				format.getUrl());
	}
	
	private static DownloadFormat toFormat(RequestedFormat format) {
		String type = "unknown";
		
		if (!StringUtils.isEmpty(format.getVcodec()))
			type = format.getVcodec();
		else if (!StringUtils.isEmpty(format.getAcodec()))
			type = format.getAcodec();
		
		
		final long expectedSize = format.getFilesize() != null ? format.getFilesize().longValue() : getFilesizeFromUrl(format.getUrl());
		return new DownloadFormat(type,
				format.getFormatId(),
				expectedSize,
				format.getUrl());
	}
	
	private static long getFilesizeFromUrl(String urlstring) {
		final URL url;
		try {
			url = new URL(urlstring);
		} catch (MalformedURLException e) {
			LOGGER.error("error getting filesize",e);
			return 0;
		}
		URLConnection conn = null;
		try {
			conn = url.openConnection();
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).setRequestMethod("HEAD");
			}
			conn.getInputStream();
			return conn.getContentLength();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).disconnect();
			}
		}
	}
	
	
	private YdlDownloadTask getYdlTask() {
		YdlDownloadTask result = ydlTaskReference.get();
		if (result == null) {
			
			YdlDownloadTask.YdlDownloadTaskBuilder builder = YdlDownloadTask.builder()
					.setUrl(getTask().getSourceUrl())
					.setPathToYdl(pathToYdl);
			
			if (getTask().getSourceUrl().contains("youtube.com")) {
				builder.setForceMp4(forceMp4OnYoutube);
			}
			
			result = builder
					.build();
			if (!ydlTaskReference.compareAndSet(null, result)) {
				return ydlTaskReference.get();
			}
		}
		return result;
	}
	
	private void handleError(YdlDownloadTask ydlDownloadTask, YdlStatusMessage ydlStatusMessage) {
	}
	
	private void handlePrepared(YdlDownloadTask ydlDownloadTask, SingleInfoJsonMetadataAccessor singleInfoJsonMetadataAccessor) {
	}
	
	@Override
	public Observable addObserver(Observer<?> observer) {
		return observableTemplate.addObserver(observer);
		
	}
	
	@Override
	public Observable removeObserver(Observer<?> observer) {
		return observableTemplate.removeObserver(observer);
	}
}