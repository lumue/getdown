package io.github.lumue.getdown.core.download.files;

import io.github.lumue.getdown.core.download.task.DownloadTask;
import reactor.core.publisher.Flux;
import rx.Observable;
import rx.Scheduler;
import rx.fileutils.FileSystemEvent;
import rx.fileutils.FileSystemEventKind;
import rx.fileutils.FileSystemWatcher;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Manage temporary download files
 * Created by lm on 06.12.16.
 */
public class WorkPathManager  {

	private final Path root;



	public WorkPathManager(Path root) throws IOException {
		this.root = Objects.requireNonNull(root);
		if (!root.toFile().exists())
			Files.createDirectory(this.root);
	}



	public Path createPath(String taskId) throws IOException {
		Path path = root.resolve(taskId);
		Files.createDirectory(path);
		return path;
	}

}
