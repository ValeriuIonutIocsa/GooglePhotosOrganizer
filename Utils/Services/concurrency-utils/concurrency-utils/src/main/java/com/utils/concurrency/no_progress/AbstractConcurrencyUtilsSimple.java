package com.utils.concurrency.no_progress;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.utils.concurrency.AbstractConcurrencyUtils;

public abstract class AbstractConcurrencyUtilsSimple extends AbstractConcurrencyUtils {

	protected AbstractConcurrencyUtilsSimple(
			final int threadCount) {

		super(threadCount);
	}

	@Override
	public void executeMultiThreadedTask(
			final List<Runnable> runnableList) {

		if (!runnableList.isEmpty()) {

			printInitMessages();

			final ExecutorService executorService;
			final int threadCount = getThreadCount();
			if (threadCount <= 0) {
				executorService = Executors.newCachedThreadPool();
			} else {
				executorService = Executors.newFixedThreadPool(threadCount);
			}

			final List<Future<?>> futureList = new ArrayList<>();
			for (final Runnable runnable : runnableList) {
				submitCallable(runnable, executorService, futureList);
			}

			for (final Future<?> future : futureList) {
				futureGet(future);
			}

			executorService.shutdown();
		}
	}

	protected abstract void submitCallable(
			Runnable runnable,
			ExecutorService executorService,
			List<Future<?>> futureList);
}
