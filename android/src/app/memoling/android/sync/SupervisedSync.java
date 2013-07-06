package app.memoling.android.sync;

import java.util.ArrayList;

import android.content.Context;

import app.memoling.android.helper.AppLog;
import app.memoling.android.sync.ConflictResolve.OnConflictResolve;

public abstract class SupervisedSync<T> {

	public SupervisedSync(Context context, OnConflictResolve<T> onConflict, OnSyncComplete onComplete) {
		m_context = context;
		m_onConflictResolve = onConflict;
		m_onSyncComplete = onComplete;
	}

	public interface OnSyncComplete {
		void onComplete(boolean result);
	}

	// Preparation code, executed first
	protected void prepare() throws Exception {
	}

	// Clean up code, executed after onSuccess returns
	protected void clean() throws Exception {
	}

	// Should return internal object which is considered as the same as external
	// 'object' one
	protected abstract T contains(T object) throws Exception;

	protected abstract ArrayList<T> getInternal();

	protected abstract ArrayList<? extends T> getExternal();

	protected abstract T getNewer(T internal, T external);
	
	protected abstract boolean submitTransaction(ArrayList<T> internalToDelete, ArrayList<T> externalToAdd);

	public void sync() {

		ArrayList<T> toDelete = new ArrayList<T>();
		ArrayList<T> toAdd = new ArrayList<T>();
		
		try {
			SupervisedSync.this.prepare();

			ConflictResolve resolve = new ConflictResolve();

			for (T external : getExternal()) {
				T internal = contains(external);

				// Get resolution
				if (!resolve.hasFlag(ConflictResolve.ForAll)) {
					if (internal != null) {
						resolve = getOnConflictResolve().onConflict(internal, external);
					}

					if (resolve.hasFlag(ConflictResolve.Stop)) {
						if (resolve.hasFlag(ConflictResolve.ForAll)) {
							getOnSyncComplete().onComplete(false);
							return;
						}
						continue;
					}

				}
				
				// No conflict
				if(internal == null) {
					toAdd.add(external);
				} else {
					if(resolve.hasFlag(ConflictResolve.TakeNewer)) {
						T result = getNewer(internal, external);
						if(result == null) {
							getOnSyncComplete().onComplete(false);
						}
						if(result == external) {
							toDelete.add(internal);
							toAdd.add(external);
						}
					} else if(resolve.hasFlag(ConflictResolve.TakeFile)) {
						toDelete.add(internal);
						toAdd.add(external);
					} else if(resolve.hasFlag(ConflictResolve.TakeDatabase)) {
						// Do nothing
					}
				}

			}
			
			boolean result = submitTransaction(toDelete, toAdd);			
			getOnSyncComplete().onComplete(result);
			
		} catch (Exception ex) {
			AppLog.e("SupervisedSync", "sync", ex);
			getOnSyncComplete().onComplete(false);
		} finally {
			try {
				clean();
			} catch (Exception ex) {
				AppLog.e("SupervisedSync", "sync - close", ex);
			}
		}

	}

	private Context m_context;

	protected final Context getContext() {
		return m_context;
	}

	protected final void setContext(Context context) {
		m_context = context;
	}

	private OnConflictResolve<T> m_onConflictResolve;

	public final OnConflictResolve<T> getOnConflictResolve() {
		return m_onConflictResolve;
	}

	protected final void setOnConflictResolve(OnConflictResolve<T> onConflict) {
		m_onConflictResolve = onConflict;
	}

	public OnSyncComplete m_onSyncComplete;

	public final OnSyncComplete getOnSyncComplete() {
		return m_onSyncComplete;
	}

	protected final void setOnSyncComplete(OnSyncComplete onComplete) {
		m_onSyncComplete = onComplete;
	}

}