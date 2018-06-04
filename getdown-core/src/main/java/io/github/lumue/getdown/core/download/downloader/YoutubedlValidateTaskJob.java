package io.github.lumue.getdown.core.download.downloader;

import io.github.lumue.getdown.core.download.task.DownloadFormat;
import io.github.lumue.getdown.core.download.task.DownloadTask;
import io.github.lumue.getdown.core.download.task.ValidateTaskJob;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.metadata.single_info_json.Format;
import io.github.lumue.ydlwrapper.metadata.single_info_json.HttpHeaders;
import io.github.lumue.ydlwrapper.metadata.single_info_json.RequestedFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class YoutubedlValidateTaskJob extends ValidateTaskJob  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YoutubedlValidateTaskJob.class);
	private transient AtomicReference<YdlDownloadTask> ydlTaskReference = new AtomicReference<>(null);
	private boolean forceMp4OnYoutube = true;
	
	private String pathToYdl = "/usr/local/bin/youtube-dl";

	
	
	public YoutubedlValidateTaskJob(DownloadTask task) {
		super(task);
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
						getTask().setExpectedSize(selectedFormats.stream().mapToLong(DownloadFormat::getExpectedSize).sum());
						getTask().setTargetExtension(ydlInfoJson.getExt());
						getYdlTask().getYdlInfoJsonAsText().ifPresent(t->getTask().setInfoJsonString(t));
					}
			);
			
			setValidatedState();
		} catch (Exception e) {
			setInvalidState();
			LOGGER.error("error validating task ", e);
		}
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
		DownloadFormat.Type type = DownloadFormat.Type.MERGED;
		String codec=format.getExt();
		if (!StringUtils.isEmpty(format.getVcodec())&&!StringUtils.isEmpty(format.getAcodec())){
			if ("none".equals(format.getAcodec())) {
				type = DownloadFormat.Type.VIDEO;
				codec=format.getVcodec();
			}
			else if("none".equals(format.getVcodec())) {
				type = DownloadFormat.Type.AUDIO;
				codec=format.getAcodec();
			}
		}
		return new DownloadFormat(type,
				format.getFormatId(),
				format.getFilesize() != null ? format.getFilesize().longValue() : getFilesizeFromUrl(format.getUrl()),
				format.getUrl(),
				toMap(format.getHttpHeaders()),
				codec,
				format.getExt());
	}
	
	private static Map<String, String> toMap(HttpHeaders httpHeaders) {
		final HashMap<String,String> map = new HashMap<>();
		map.put("Accept",httpHeaders.getAccept());
		map.put("AcceptCharset",httpHeaders.getAcceptCharset());
		map.put("AcceptEncoding",httpHeaders.getAcceptEncoding());
		map.put("AcceptLanguage",httpHeaders.getAcceptLanguage());
		httpHeaders.getAdditionalProperties().entrySet().forEach(
				e->map.put(e.getKey(),e.getValue().toString())
		);
		map.put("UserAgent",httpHeaders.getUserAgent());
		return map;
	}
	
	private static DownloadFormat toFormat(RequestedFormat format) {
		
		DownloadFormat.Type type = DownloadFormat.Type.MERGED;
		String codec=format.getExt();
		if (!StringUtils.isEmpty(format.getVcodec())&&!StringUtils.isEmpty(format.getAcodec())){
			if ("none".equals(format.getAcodec())) {
				type = DownloadFormat.Type.VIDEO;
				codec=format.getVcodec();
			}
			else if("none".equals(format.getVcodec())) {
				type = DownloadFormat.Type.AUDIO;
				codec=format.getAcodec();
			}
		}
		
		
		return new DownloadFormat(type,
				format.getFormatId(),
				format.getFilesize() != null ? format.getFilesize().longValue() : getFilesizeFromUrl(format.getUrl()),
				format.getUrl(),
				toMap(format.getHttpHeaders()),
				codec,
				format.getExt());
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
			return -1L;
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
	

}