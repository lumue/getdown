package io.github.lumue.getdown.core.download.files;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;


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
		Path path = getPath(taskId);
		Files.createDirectory(path);
		return path;
	}

	public Path getPath(String taskId) {
		return root.resolve(taskId);
	}
}
