package io.github.lumue.getdown.core.download.task;

import io.github.lumue.getdown.core.common.util.Observer;
import io.github.lumue.getdown.core.download.downloader.youtubedl.YoutubedlValidateTaskJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.bus.Event;
import reactor.bus.EventBus;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Takes care of jobs that should be executed.
 * jobs are added to a queue, and then taken one by one and passed to
 * the prepare and download executors.
 */
public class AsyncValidateTaskRunner implements Runnable {


	private final ThreadPoolTaskExecutor prepareExecutor;

	private final AtomicBoolean shouldRun = new AtomicBoolean(false);

	private final EventBus eventBus;
	
	private final DownloadTaskRepository taskRepository;

	//queue capacity can be small.. running a job should be virtually nonblocking, since prepare an download are implemented as async operations
	private final BlockingQueue<ValidateTaskJob> taskQueue = new PriorityBlockingQueue<>(10, new Comparator<ValidateTaskJob>() {
		@Override
		public int compare(ValidateTaskJob o1, ValidateTaskJob o2) {
			return o1.getTask().getCreationTime().compareTo(o2.getTask().getCreationTime());
		}
	});


	private static Logger LOGGER = LoggerFactory.getLogger(AsyncValidateTaskRunner.class);
	private final Executor jobRunner;


	public AsyncValidateTaskRunner(
			int maxThreadsPrepare, EventBus eventBus, DownloadTaskRepository taskRepository) {
		super();
		this.prepareExecutor = executor("validate-task-executor",maxThreadsPrepare);
		this.eventBus = eventBus;
		this.taskRepository = taskRepository;
		this.jobRunner = executor("validate-job-runner",1);
	}


	private void runValidation(final ValidateTaskJob validateJob) {
		AsyncValidateTaskRunner.LOGGER.debug("validating " +validateJob);
			CompletableFuture.runAsync(validateJob, prepareExecutor);
			
	}

	public void submitTask(final DownloadTask task) {
		String jobUrl = task.getSourceUrl();
		AsyncValidateTaskRunner.LOGGER.debug("queueing task " + jobUrl + " for validation");
		final YoutubedlValidateTaskJob validateTaskJob = new YoutubedlValidateTaskJob(task);
		validateTaskJob.addObserver((Observer<YoutubedlValidateTaskJob>) observable -> {
			eventBus.notify("tasks-state", Event.wrap(Objects.requireNonNull(observable.getTask())));
			taskRepository.update(observable.getTask());
		});
		taskQueue.add(validateTaskJob);
	}
	
	
	
	
	
	private static ThreadPoolTaskExecutor executor(String name, Integer threads) {
		final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setThreadNamePrefix(name);
		threadPoolTaskExecutor.setThreadGroupName(name);
		threadPoolTaskExecutor.setCorePoolSize(threads);
		threadPoolTaskExecutor.setMaxPoolSize(threads);
		return threadPoolTaskExecutor;
	}

	@Override
	public void run() {
		while (shouldRun.get()) {
			try {
				runValidation(taskQueue.take());
			} catch (InterruptedException e) {
				LOGGER.warn("interrupted while accessing task validation queue", e);
			}
		}
	}

	public void stop() {
		shouldRun.compareAndSet(true, false);
	}


	
	@PostConstruct
	public void start() {
		shouldRun.compareAndSet(false, true);
		jobRunner.execute(this);
	}
}
