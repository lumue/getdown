package io.github.lumue.getdown.core.download.job;

import java.util.Comparator;
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

	private final AtomicBoolean shouldRun=new AtomicBoolean(false);


	//queue capacity can be small.. running a job should be virtually nonblocking, since prepare an download are implemented as async operations
	private final BlockingQueue<DownloadJob> jobQueue=new PriorityBlockingQueue<>(10, new Comparator<DownloadJob>() {
		@Override
		public int compare(DownloadJob o1, DownloadJob o2) {
			return o1.getIndex().compareTo(o2.getIndex());
		}
	});



	private static Logger LOGGER = LoggerFactory.getLogger(AsyncDownloadJobRunner.class);
	private final Executor jobRunner;


	public AsyncDownloadJobRunner(
			int maxThreadsPrepare,
			int maxThreadsDownload) {
		super();
		this.downloadExecutor = executor(maxThreadsDownload);
		this.prepareExecutor  =executor(maxThreadsPrepare);
		this.jobRunner=executor(1);
	}



	private void runJob(final DownloadJob job) {
		String jobUrl = job.getUrl();
		AsyncDownloadJobRunner.LOGGER.debug("starting " + jobUrl);



		DownloadJob.DownloadJobState jobState = job.getState();
		if(!job.isPrepared()){
			AsyncDownloadJobRunner.LOGGER.debug("submitting " + jobUrl +" for prepare");
			this.prepareExecutor.submit(job);
		}

		AsyncDownloadJobRunner.LOGGER.debug("submitting " + jobUrl +" for download");
		this.downloadExecutor.submit(job);
	}

	public void submitJob(final DownloadJob job) {
		String jobUrl = job.getUrl();
		job.waiting();
		AsyncDownloadJobRunner.LOGGER.debug("queueing " + jobUrl+" for execution");
		jobQueue.add(job);
	}





	public void cancelJob(DownloadJob job) {
		job.cancel();
	}

	private static ScheduledThreadPoolExecutor executor(Integer threads){
		return (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(threads);

	}

	@Override
	public void run() {
		while(shouldRun.get()){
			try {
				runJob(jobQueue.take());
			} catch (InterruptedException e) {
				LOGGER.warn("interrupted while accessing job queue",e);
			}
		}
	}

	public void stop(){
		shouldRun.compareAndSet(true,false);
	}



	public Stream<DownloadJob> streamDownloadingJobs() {
		return downloadExecutor.getQueue()
		.stream()
		.map(r->(DownloadJob)r);
	}

	public Stream<DownloadJob> streamPreparingJobs() {
		return prepareExecutor.getQueue()
				.stream()
				.map(r->(DownloadJob)r);
	}

	public Stream<DownloadJob> streamQueuedJobs() {
		return jobQueue.stream();
	}

	@PostConstruct
	public void start(){
		shouldRun.compareAndSet(false,true);
		jobRunner.execute(this);
	}
}
