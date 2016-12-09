package io.github.lumue.getdown.core.download.job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

/**
 * Takes care of jobs that should be executed.
 * jobs are added to a queue, and then taken one by one and passed to
 * the prepare and download executors.
 */
public class AsyncDownloadJobRunner implements Runnable {

	private final ScheduledThreadPoolExecutor downloadExecutor;

	private final ScheduledThreadPoolExecutor prepareExecutor;

	private final ScheduledThreadPoolExecutor postprocessExecutor;

	private final AtomicBoolean shouldRun = new AtomicBoolean(false);


	private final List<DownloadJob> running = new ArrayList<>();

	private final List<DownloadJob> done = new ArrayList<>();

	//queue capacity can be small.. running a job should be virtually nonblocking, since prepare an download are implemented as async operations
	private final BlockingQueue<DownloadJob> jobQueue = new PriorityBlockingQueue<>(10, new Comparator<DownloadJob>() {
		@Override
		public int compare(DownloadJob o1, DownloadJob o2) {
			return o1.getIndex().compareTo(o2.getIndex());
		}
	});


	private static Logger LOGGER = LoggerFactory.getLogger(AsyncDownloadJobRunner.class);
	private final Executor jobRunner;


	public AsyncDownloadJobRunner(
			int maxThreadsPrepare,
			int maxThreadsDownload,
			int maxThreadsPostprocess) {
		super();
		this.downloadExecutor = executor(maxThreadsDownload);
		this.prepareExecutor = executor(maxThreadsPrepare);
		this.postprocessExecutor = executor(maxThreadsPostprocess);
		this.jobRunner = executor(1);
	}


	private void runJob(final DownloadJob job) {
		String jobUrl = job.getUrl();
		AsyncDownloadJobRunner.LOGGER.debug("starting " + jobUrl);
		running.add(job);
		if (!job.isPrepared()) {
			CompletableFuture.runAsync(job::prepare, prepareExecutor)
					.thenRunAsync(job, downloadExecutor)
					.thenRunAsync(job::postProcess,postprocessExecutor)
					.thenRun(() -> {
						running.remove(job);
						done.add(job);
					});
		} else
			CompletableFuture.runAsync(job, downloadExecutor)
					.thenRunAsync(job::postProcess,postprocessExecutor)
					.thenRun(() -> {
						running.remove(job);
						done.add(job);
					});
	}

	public void submitJob(final DownloadJob job) {
		String jobUrl = job.getUrl();
		job.waiting();
		AsyncDownloadJobRunner.LOGGER.debug("queueing " + jobUrl + " for execution");
		jobQueue.add(job);
	}


	public void cancelJob(DownloadJob job) {
		job.cancel();
	}



	private static ScheduledThreadPoolExecutor executor(Integer threads) {
		return (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(threads);

	}

	@Override
	public void run() {
		while (shouldRun.get()) {
			try {
				runJob(jobQueue.take());
			} catch (InterruptedException e) {
				LOGGER.warn("interrupted while accessing job queue", e);
			}
		}
	}

	public void stop() {
		shouldRun.compareAndSet(true, false);
	}


	public Stream<DownloadJob> streamRunning() {
		return running.stream();
	}


	public Stream<DownloadJob> streamQueued() {
		return jobQueue.stream();
	}

	public Stream<DownloadJob> streamDone() {
		return done.stream();
	}

	@PostConstruct
	public void start() {
		shouldRun.compareAndSet(false, true);
		jobRunner.execute(this);
	}
}
