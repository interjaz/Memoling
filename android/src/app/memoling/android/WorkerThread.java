package app.memoling.android;

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

// Unifies thread pool workers on android across different versions
// Starting from API11+ you can use AsyncTask<?,?,?>.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params)
public abstract class WorkerThread<TExecute, TProgress, TResult> {

	private final static WorkerThreadPool s_threadPool;
	private final static Handler s_uiHandler;

	private final static int THREAD_FINISHED = 0;
	private final static int THREAD_PUBLISH = 1;

	private boolean m_cancelled;
	private Thread m_workerThread;

	private TExecute[] m_params;

	static {
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
		Message msg = s_uiHandler.obtainMessage(THREAD_PUBLISH, new WorkerThreadResult<TProgress[]>(WorkerThread.this, progress));
		msg.sendToTarget();	
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
			super.handleMessage(msg);
			
			Object[] progress;
			Object result;
			WorkerThread<Object, Object, Object> workerThread;

			WorkerThreadResult<Object> threadResult = (WorkerThreadResult<Object>)msg.obj;

			workerThread = threadResult.m_thread;

			switch (msg.what) {
			case THREAD_PUBLISH:
				progress = (Object[])threadResult.m_result;
				workerThread.onProgressUpdate(progress);
				break;
			case THREAD_FINISHED:
				result = threadResult.m_result;
				workerThread.onPostExecute(result);
				break;
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

			Message msg = s_uiHandler.obtainMessage(THREAD_FINISHED, new WorkerThreadResult<TResult>(WorkerThread.this, result));
			msg.sendToTarget();
		}

	}

	private static class WorkerThreadResult<TResult> {
		public WorkerThread m_thread;
		public TResult m_result;
		
		public WorkerThreadResult(WorkerThread thread, TResult result) {
			m_thread = thread;
			m_result = result;
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
