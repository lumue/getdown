package io.github.lumue.getdown.application;

import io.github.lumue.getdown.downloader.AsyncContentDownloader;
import io.github.lumue.getdown.downloader.ContentDownloader;
import io.github.lumue.getdown.downloader.ContentDownloader.DownloadState;
import io.github.lumue.getdown.downloader.DownloadProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class Main {

	public static void main(String[] args) {

		ContentDownloader asyncContentDownloader = AsyncContentDownloader.builder().build();

		String url = args[0];
		String filename = args[1];

		File file = new File(filename);
		OutputStream targetStream;
		DownloadProgressListener progress = new DownloadProgressListener();

		try {
			targetStream = new FileOutputStream(file);
			asyncContentDownloader.downloadContent(URI.create(url), targetStream, progress);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		while(!DownloadState.RUNNING.equals(progress.getState()))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		String size = progress.getSize() != null ? Long.toString(progress.getSize()) : "unknown";
		long downloadedSize, lastDownloadedSize = 0L;
		while (!DownloadState.FINISHED.equals(progress.getState())) {

			if (DownloadState.ERROR.equals(progress.getState())) {
				progress.getError().printStackTrace();
				System.exit(1);
			}

			downloadedSize = progress.getDownloadedSize();
			if (downloadedSize != lastDownloadedSize) {
				System.out.println(downloadedSize + "/" + size + " bytes downloaded");
				lastDownloadedSize = downloadedSize;
			}
		}

		System.out.println("done");
		System.exit(0);
	}

}
