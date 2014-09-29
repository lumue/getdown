package io.github.lumue.getdown;

import io.github.lumue.getdown.download.AsyncContentDownloader;
import io.github.lumue.getdown.download.ContentDownloader.DownloadProgress;
import io.github.lumue.getdown.download.ContentDownloader.DownloadState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;

public class Main {

	public static void main(String[] args) {
		AsyncContentDownloader asyncContentDownloader = new AsyncContentDownloader();
		File file = new File("/home/lm/git/getdown.git/test.zip");
		OutputStream targetStream;
		DownloadProgress progress;
		try {
			targetStream = new FileOutputStream(file);
			progress = asyncContentDownloader.downloadContent(URI.create("https://services.gradle.org/distributions/gradle-2.1-all.zip"),
					targetStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		while(!DownloadState.RUNNING.equals(progress.getState()))
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String size = progress.getSize() != null ? Long.toString(progress.getSize()) : "unknown";

		while (!DownloadState.FINISHED.equals(progress.getState())) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (DownloadState.ERROR.equals(progress.getState())) {
				progress.getError().printStackTrace();
				System.exit(1);
			}

			System.out.println(progress.getDownloadedSize()+"/"+size+" bytes downloaded");
		}

		System.out.println("done");
	}

}
