package io.github.lumue.getdown.core.download.downloader;

import java.util.Map;
import java.util.function.Consumer;

public interface FileDownloader {
	
	void downloadFile(String sourceUrl, Map<String,String> headers, String filename, Consumer<Long> byteCountConsumer);
	
}
