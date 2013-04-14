package com.interjaz;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.Message;

public abstract class WorkerThread<TExecute, TProgress, TResult> {

	private final static WorkerThreadPool s_threadPool;
	private final static Handler s_uiHandler;

	private final static int THREAD_FINISHED = 0;
	private final static int THREAD_PUBLISH = 1;

	private final static Object s_lock;
	private boolean m_cancelled;
	private Thread m_workerThread;

	private TExecute[] m_params;
	private static Object s_result;
	private static Object s_progress;
	private static Object s_workerThread;

	static {
		s_lock = new Object();
		s_uiHandler = new UiHandler();
		s_threadPool = new WorkerThreadPool();
	}

	protected abstract TResult doInBackground(TExecute... params);

	protected void onProgressUpdate(TProgress... progress) {
	}

	protected void onPostExecute(TResult result) {
	}

	public void execute(TExecute... params) {
		m_params = params;
		onPreExecute();
		s_threadPool.execute(new WorkerThreadRunnable());
	}

	public void publishProgress(TProgress... progress) {
		synchronized (s_lock) {
			s_workerThread = WorkerThread.this;
			s_progress = progress;
			s_uiHandler.sendEmptyMessage(THREAD_PUBLISH);

			try {
				s_lock.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	public void cancel(boolean mayInterruptIfRunning) {
		if (!isCancelled()) {
			m_cancelled = true;

			if (m_workerThread != null) {
				m_workerThread.interrupt();
			}
		}
	}

	public boolean isCancelled() {
		return m_cancelled;
	}
	
	protected void onPreExecute() {	
	}

	private static class UiHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			synchronized (s_lock) {
				super.handleMessage(msg);

				switch (msg.what) {
				case THREAD_PUBLISH:
					((WorkerThread<Object, Object, Object>) s_workerThread).onProgressUpdate(s_progress);
					break;
				case THREAD_FINISHED:
					((WorkerThread<Object, Object, Object>) s_workerThread).onPostExecute(s_result);
					break;
				}

				s_lock.notify();
			}

		}

	}

	private class WorkerThreadRunnable implements Runnable {

		@Override
		public void run() {
			if (m_cancelled) {
				return;
			}
			m_workerThread = Thread.currentThread();
			
			TResult result = doInBackground(m_params);

			synchronized (s_lock) {
				s_workerThread = WorkerThread.this;
				s_result = result;
				s_uiHandler.sendEmptyMessage(THREAD_FINISHED);
				try {
					s_lock.wait();
				} catch (InterruptedException e) {
				}
			}
		}

	}

	private static class WorkerThreadPool extends ThreadPoolExecutor {

		private final static BlockingQueue<Runnable> s_queue;
		private final static WorkerThreadFactory s_factory;
		private final static TimeUnit s_unit;
		private final static long s_keepAliveTime;
		private final static int s_corePoolSize;
		private final static int s_maximumPoolSize;
		private final static AtomicInteger s_threadId;

		static {
			s_keepAliveTime = 1000;
			s_unit = TimeUnit.MILLISECONDS;
			s_corePoolSize = 5;
			s_maximumPoolSize = Integer.MAX_VALUE;
			s_queue = new LinkedBlockingQueue<Runnable>();
			s_factory = new WorkerThreadFactory();
			s_threadId = new AtomicInteger();
		}

		public WorkerThreadPool() {
			super(s_corePoolSize, s_maximumPoolSize, s_keepAliveTime, s_unit, s_queue, s_factory);
		}

		public static class WorkerThreadFactory implements ThreadFactory {

			@Override
			public Thread newThread(Runnable runnable) {
				int threadId = s_threadId.incrementAndGet();
				Thread thread = new Thread(runnable);
				thread.setName("WorkerThread #" + Integer.toString(threadId));

				return thread;
			}

		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);

			if (t == null && r instanceof Future<?>) {
				try {
					Future<?> future = (Future<?>) r;
					if (future.isDone())
						future.get();
				} catch (CancellationException ce) {
					t = ce;
				} catch (ExecutionException ee) {
					t = ee.getCause();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
			}
			if (t != null) {
				throw new RuntimeException(t);
			}
		}

	}

}
