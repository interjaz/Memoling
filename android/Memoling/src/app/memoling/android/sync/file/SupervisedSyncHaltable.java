package app.memoling.android.sync.file;

import android.content.Context;
import android.os.Message;

import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.file.ConflictResolve.OnConflictResolve;
import app.memoling.android.sync.file.ConflictResolve.OnConflictResolveHaltable;
import app.memoling.android.thread.HaltableThread;
import app.memoling.android.thread.HaltableThread.OnHandleMessageHaltable;

public abstract class SupervisedSyncHaltable<T> extends SupervisedSync<T> {

	private Object m_lock = new Object();

	private final static int ConflictMsg = 0;
	private final static int CompleteMsg = 1;

	private HaltableThread<ConflictResolve> m_thread;

	private T m_conflictObjectInternal;
	private T m_conflictObjectExternal;

	private OnSyncComplete m_onSyncCompleteHaltable;

	private boolean m_completeResult;

	public SupervisedSyncHaltable(Context context, OnConflictResolveHaltable<T> onConflict, OnSyncComplete onComplete) {
		super(context, null, null);
		m_onSyncCompleteHaltable = onComplete;
		m_onConflictResolveHaltable = onConflict;
	}

	@Override
	protected void prepare() throws Exception {
		super.prepare();

		setOnConflictResolve(new OnConflictResolve<T>() {

			@Override
			public ConflictResolve onConflict(T internal, T external) {
				synchronized (m_lock) {
					m_thread.setObject(new ConflictResolve());

					m_conflictObjectInternal = internal;
					m_conflictObjectExternal = external;

					m_thread.getUiHandler().sendEmptyMessage(ConflictMsg);
					try {
						m_thread.halt();
					} catch (InterruptedException ex) {
						AppLog.e("SupervisedSyncHaltable", "onConflict", ex);
					}
					return m_thread.getObject();
				}
			}
		});

		setOnSyncComplete(new OnSyncComplete() {
			@Override
			public void onComplete(boolean result) {
				synchronized (m_lock) {
					m_completeResult = result;
					m_thread.getUiHandler().sendEmptyMessage(CompleteMsg);
				}
			}
		});
	}

	@Override
	protected void clean() throws Exception {
		super.clean();
		m_thread.clean();
	}

	@Override
	public void sync() {

		m_thread = new HaltableThread<ConflictResolve>() {

			@Override
			public void run() {
				super.run();
				SupervisedSyncHaltable.super.sync();
			}
		};

		m_thread.prepare(new OnHandleMessageHaltable<ConflictResolve>() {

			@Override
			public void onHandleMessage(Message message, ConflictResolve waitObject) {

				if (message.what == ConflictMsg) {
					getOnConflictResolveHaltable().onConflict(m_conflictObjectInternal, m_conflictObjectExternal,
							waitObject, waitObject);

				} else if (message.what == CompleteMsg) {
					m_onSyncCompleteHaltable.onComplete(m_completeResult);
				}
			}

		});

		m_thread.start();
	}

	private OnConflictResolveHaltable<T> m_onConflictResolveHaltable;

	protected OnConflictResolveHaltable<T> getOnConflictResolveHaltable() {
		return m_onConflictResolveHaltable;
	}

	protected void setOnConflictResolveHaltable(OnConflictResolveHaltable<T> onConflict) {
		m_onConflictResolveHaltable = onConflict;
	}
}
