package app.memoling.android.thread;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import app.memoling.android.R;
import app.memoling.android.helper.AppLog;
import app.memoling.android.ui.ResourceManager;

public class SpinThread<T> {

	private WorkerThread<Void, Void, Void> m_thread;
	
	private AtomicBoolean m_markedCompleted;
	
	private List<Runnable> m_extraWork;
	
	private T m_result;
	
	public interface SpinRunnable<T> {
		public void run(SpinThread<T> spinThread);
	}
	
	public interface SpinRunnableResult<T> {
		public void run(T result);
	}

	public SpinThread(Context context, final SpinRunnable<T> runnable, final SpinRunnableResult<T> onComplete) {

		final WeakReference<Context> refContext = new WeakReference<Context>(
				context);
		
		m_markedCompleted = new AtomicBoolean(false);
		m_extraWork = new ArrayList<Runnable>();

		m_thread = new WorkerThread<Void, Void, Void>() {

			private AlertDialog m_dialog;

			@Override
			protected Void doInBackground(Void... params) {
				runnable.run(SpinThread.this);
				
				while(!m_markedCompleted.get()) {
					try {
						
						Runnable extraRunnable = null;
						synchronized(m_extraWork) {
							if(m_extraWork.size() > 0) {
								extraRunnable = m_extraWork.remove(0);
							}
						}
						
						if(extraRunnable == null) {
							Thread.sleep(500);
						} else {
							extraRunnable.run();
						}
						
					} catch (InterruptedException ex) {
						AppLog.e("SpinThread", "Sleep Interrupted", ex);
					}
				}
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (m_dialog != null) {
					m_dialog.dismiss();
				}
				
				if(onComplete != null) {
					onComplete.run(m_result);
				}
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();

				Context ctx = refContext.get();
				if (ctx != null) {
					LayoutInflater inflater = (LayoutInflater) ctx
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View view = inflater.inflate(R.layout.dialog_spinthread,
							null);
					TextView textView1 = (TextView)view.findViewById(R.id.textView1);
					textView1.setTypeface(new ResourceManager(ctx).getLightFont());

					m_dialog = new AlertDialog.Builder(ctx)
							.setView(view).setCancelable(false).create();

					m_dialog.show();
				}
			}

		};
	}

	public void start() {
		m_thread.execute();
	}
	
	public void setResult(T result) {
		m_result = result;
	}
	
	public void setCompleted() {
		m_markedCompleted.set(true);
	}
	
	public void addWork(Runnable runnable) {
		synchronized(m_extraWork) {
			m_extraWork.add(runnable);
		}
	}

}
