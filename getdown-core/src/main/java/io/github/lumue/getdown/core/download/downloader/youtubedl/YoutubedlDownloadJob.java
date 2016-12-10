package io.github.lumue.getdown.core.download.downloader.youtubedl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lumue.getdown.core.download.job.Download;
import io.github.lumue.getdown.core.download.job.DownloadJob;

import io.github.lumue.getdown.core.download.job.DownloadProgress;
import io.github.lumue.ydlwrapper.download.YdlDownloadTask;
import io.github.lumue.ydlwrapper.download.YdlFileDownload;
import io.github.lumue.ydlwrapper.metadata.single_info_json.SingleInfoJsonMetadataAccessor;
import io.github.lumue.ydlwrapper.metadata.statusmessage.YdlStatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.lumue.getdown.core.download.job.DownloadState.*;

/**
 * Delegates actual downloading to {@link YdlDownloadTask}
 */
public class YoutubedlDownloadJob extends Download {

	private static final Logger LOGGER = LoggerFactory.getLogger(YoutubedlDownloadJob.class);
	private transient AtomicReference<YdlDownloadTask> downloadTask=new AtomicReference<>(null);
	private boolean forceMp4OnYoutube=true;


	@JsonCreator
	private YoutubedlDownloadJob(
			@JsonProperty("url") String url,
			@JsonProperty("handle") String handle,
			@JsonProperty("state") DownloadJob.DownloadJobState downloadJobState,
			@JsonProperty("downloadProgress") DownloadProgress downloadProgress,
			@JsonProperty("name") String name,
			@JsonProperty("host") String host,
			@JsonProperty("index") Long index,
			@JsonProperty("targetPath") String targetPath) {
		super(url,handle,downloadJobState,downloadProgress,name,host,index, targetPath);
	}

	private YoutubedlDownloadJob(String handle,
								 String name,
	                             String url,
	                             String host,
	                             String downloadPath,
	                             Long index,
	                             String targetPath) {
		super(name, url, host, handle,index,targetPath);
		setDownloadPath(downloadPath);
	}

	@Override
	public void prepare() {
		if(DownloadJobState.PREPARING.equals(downloadJobState))
			return;
		preparing();
		try {
			getDownloadTask().prepare();
		} catch (Exception e) {
			handleError(e);
		}
	}

	private YdlDownloadTask getDownloadTask() {
		YdlDownloadTask result = downloadTask.get();
		if (result == null) {
			YdlDownloadTask.YdlDownloadTaskBuilder builder = YdlDownloadTask.builder()
					.setUrl(getUrl())
					.setOutputFolder(getDownloadPath())
					.setWriteInfoJson(true)
					.setPathToYdl("/usr/bin/youtube-dl")
					.onStdout(this::handleMessage)
					.onStateChanged(this::handleProgress)
					.onNewOutputFile(this::handleProgress)
					.onOutputFileChange(this::handleProgress)
					.onPrepared(this::handlePrepared);

			if (getUrl().contains("youtube.com") ) {
				builder.setForceMp4(isForceMp4OnYoutube());
			}

				result = builder
						.build();
			if (!downloadTask.compareAndSet(null, result)) {
				return downloadTask.get();
			}
		}
		return result;
	}


	@Override
	public void executeDownload() {
		progress(new DownloadProgress());
		try {
			getDownloadTask().executeAsync();

				getDownloadProgress().ifPresent(p -> {
					boolean finished=false;
					while (!finished) {
						if (p.getState().equals(FINISHED)
								|| p.getState().equals(CANCELLED)
								|| p.getState().equals(ERROR)){
							finished=true;
						}
						else{
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								LOGGER.error("interrupted waiting for youtube-dl download");
								cancel();
							}
						}
					}
				});

		} catch (Throwable t) {
			handleError(t);
		}
	}

	@Override
	public void postProcess() {
		getDownloadProgress().ifPresent(downloadProgress -> {
			if (FINISHED.equals(downloadProgress.getState())) {
				String movingmsg = "moving downloaded files for download "+getName()+" to " + getTargetPath();
				LOGGER.info(movingmsg);
				try {
					message(movingmsg);
					Path target = Paths.get(getTargetPath());
					if(!Files.exists(target))
						Files.createDirectory(target);
					Files.list(Paths.get(getDownloadPath()))
							.filter(p->!Files.isDirectory(p))
							.map(p->new Path[]{p,target.resolve(p.getFileName())})
							.filter(pa ->!Files.exists(pa[1]))
							.forEach(pa -> {
								try {
									Files.move(pa[0],pa[1]);
									Files.setPosixFilePermissions(pa[1], PosixFilePermissions.fromString("rw-rw-rw-"));
								} catch (IOException e) {
									String msg = "error moving files for download "+getName()+" to " + getTargetPath();
									error(new RuntimeException(msg+":"+e.getClass().getSimpleName(),e));
									LOGGER.error(msg,e);
									throw new RuntimeException("msg", e);
								}
							});
					String movedmsg = "moved files for download "+getName()+" to " + getTargetPath();
					message(movedmsg);
					finish();
					LOGGER.info(movedmsg);
				} catch (IOException e) {
					String msg = "error moving files for download "+getName()+" to " + getTargetPath();
					error(new RuntimeException(msg+":"+e.getClass().getSimpleName(),e));
					LOGGER.error(msg,e);
					throw new RuntimeException("msg", e);
				}
			}
		});
	}

	private void handlePrepared(YdlDownloadTask ydlDownloadTask, SingleInfoJsonMetadataAccessor singleInfoJsonMetadataAccessor) {
		singleInfoJsonMetadataAccessor.getTitle().ifPresent(this::updateName);
		prepared();
	}

	private void handleMessage(YdlDownloadTask ydlDownloadTask, YdlStatusMessage ydlStatusMessage) {
		message(ydlStatusMessage.getLine());
	}

	private void handleError(Throwable t) {
		LOGGER.error("error during youtube-dl execution ", t);
		error(t);
		getDownloadProgress().ifPresent(downloadProgress -> downloadProgress.error(t));
	}

	private void handleProgress(YdlDownloadTask ydlDownloadTask, YdlDownloadTask.YdlDownloadState ydlDownloadState) {
		getDownloadProgress().ifPresent(downloadProgress -> {
			if (downloadProgress.getState().equals(WAITING)) {
				if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.EXECUTING)) {
					start();
					downloadProgress.start();
				}
			} else if (downloadProgress.getState().equals(DOWNLOADING)) {
				if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.SUCCESS)) {
					downloadProgress.finish();
					finish();
				} else if (ydlDownloadState.equals(YdlDownloadTask.YdlDownloadState.ERROR)) {
					Error error = new Error("error executing youtube-dl");
					downloadProgress.error(error);
					error(error);
				}
			}
			progress(downloadProgress);
		});
	}

	private void handleProgress(YdlDownloadTask ydlDownloadTask, YdlFileDownload ydlFileDownload) {
		getDownloadProgress().ifPresent(downloadProgress -> {
			if (ydlFileDownload.getExpectedSize() != null)
				downloadProgress.setSize(ydlFileDownload.getExpectedSize());
			if (ydlFileDownload.getDownloadedSize() != null)
				downloadProgress.updateDownloadedSize(ydlFileDownload.getDownloadedSize());
			progress(downloadProgress);
		});
	}

	@Override
	public void cancel() {
		doObserved(()-> {
			getDownloadTask().cancel();
			getDownloadProgress().ifPresent(downloadProgress -> {
				downloadProgress.cancel();
				progress(downloadProgress);
			});
			downloadJobState = DownloadJobState.CANCELLED;
			message = "download cancelled";
		});
	}



	@Override
	public boolean isPrepared() {

		return getDownloadTask().isPrepared();
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		downloadTask=new AtomicReference<>(null);
	}

	public static YoutubedlDownloadJobBuilder builder() {
		return new YoutubedlDownloadJobBuilder();
	}

	public boolean isForceMp4OnYoutube() {
		return forceMp4OnYoutube;
	}

	public void setForceMp4OnYoutube(boolean forceMp4OnYoutube) {
		this.forceMp4OnYoutube = forceMp4OnYoutube;
	}

	public static class YoutubedlDownloadJobBuilder
			extends DownloadBuilder {

		@Override
		public DownloadJob build() {
			return new YoutubedlDownloadJob(this.handle,this.name, this.url, this.host,this.downloadPath,this.index,this.targetPath);
		}

	}
}
