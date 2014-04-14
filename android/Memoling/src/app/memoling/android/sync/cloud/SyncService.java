package app.memoling.android.sync.cloud;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import app.memoling.android.adapter.SyncActionAdapter;
import app.memoling.android.adapter.SyncClientAdapter;
import app.memoling.android.entity.SyncAction;
import app.memoling.android.entity.SyncClient;
import app.memoling.android.facebook.FacebookUser;
import app.memoling.android.helper.GzipHelper;
import app.memoling.android.helper.Helper;
import app.memoling.android.preference.Preferences;
import app.memoling.android.thread.WorkerThread;
import app.memoling.android.webservice.WsFacebookUsers;
import app.memoling.android.webservice.WsFacebookUsers.ILoginComplete;
import app.memoling.android.webservice.WsSync;
import app.memoling.android.webservice.WsSync.IRegisterRequestResult;
import app.memoling.android.webservice.WsSync.ISyncRequestResult;

public class SyncService extends Service {
	
	public enum SyncState {
		Starting,
		Authorizing,
		BuildingFirstSync,
		GeneratingPackage,
		CompressingPackage,
		SendingPackage,
		DecompressingPackage,
		ApplyingPackage,
		Completed,
		AlreadyInProgress,
		NoConnectionError,
		NoClientError,
		NoAccountError,
		DataMalformed,
		ClientError,
		ServerError,
		ProcessingError,
	}
	
	public final static String ProgressDataPackageSize = "ProgressDataPackageSize";
	public final static String ProgressDataSyncActions = "ProgressDataSyncAction";
	
	private final static String SyncServiceSyncAction = "SyncAction";
	
	private static AtomicBoolean m_isSyncing = new AtomicBoolean(false);
	
	private SyncActionAdapter m_syncActionAdapter;
	private SyncClientAdapter m_syncClientAdapter;
	private SyncClient m_syncClient;
	
	private static WeakReference<ISyncProgress> m_syncProgress;
	private static Handler m_syncProgressHandler;
	
	public interface ISyncProgress {
		void onSyncProgress(SyncState state, Bundle data);
	}

	
	public static void sync(Context context, ISyncProgress syncProgress) {
		if(m_isSyncing.get()) {
			if(syncProgress != null) {
				syncProgress.onSyncProgress(SyncState.AlreadyInProgress, null);
			}
			return;
		}
		m_isSyncing.set(true);
		
		m_syncProgress = new WeakReference<ISyncProgress>(syncProgress);
		m_syncProgressHandler = new Handler();
		if(syncProgress != null) {
			syncProgress.onSyncProgress(SyncState.Starting, null);
		}
		
		Intent intent = new Intent(context, SyncService.class);
		intent.setAction(SyncServiceSyncAction);
		context.startService(intent);
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int result = Service.START_NOT_STICKY;
		
		if(intent == null) {
			// This should not happen
			stopSelf();
			return result;
		}
		
		if(intent.getAction().equals(SyncServiceSyncAction)) {

			// Check for network connection
			if(!Helper.hasInternetAccess(this)) {
				notifySyncError(SyncState.NoConnectionError);
				return result;
			}

			m_syncClientAdapter = new SyncClientAdapter(this);
			m_syncClient = m_syncClientAdapter.getCurrentSyncClient();
			if(m_syncClient == null) {
				notifySyncError(SyncState.NoClientError);
				return result;
			}
			
			m_syncActionAdapter = new SyncActionAdapter(this);
			
			executeInBackground(new Runnable() {
				@Override
				public void run() {
					authorize();
				}
			});
		}
		
		return result;
	}
	
	private void authorize() {
		notifySyncProgress(SyncState.Authorizing);
		
		Preferences preferences = new Preferences(this);
		FacebookUser user = preferences.getFacebookUser();

		// Login user
		if(user == null) {
			notifySyncError(SyncState.NoAccountError);
			return;
		}
	
		WsFacebookUsers.login(user, new ILoginComplete() {

			@Override
			public void onLoginComplete(Boolean loggedIn) {
				if(loggedIn == null) {
					notifySyncError(SyncState.NoConnectionError);
					return;
				}
				
				if(!loggedIn) {
					notifySyncError(SyncState.NoAccountError);
					return;
				}

				executeInBackground(new Runnable() {
					
					@Override
					public void run() {
						if(m_syncClient.getLastSyncServerTimestamp() == 0) {
							generateInitialPackage();
						} else {
							generatePackage();
						}
					}
					
				});
			}
			
		});
	}
	
	private void generateInitialPackage() {
			notifySyncProgress(SyncState.BuildingFirstSync);
			
			
			WsSync.registerRequest(m_syncClient, new IRegisterRequestResult() {

				@Override
				public void registerCompleted(SyncClient syncClient) {
					if(syncClient == null) {
						notifySyncError(SyncState.NoConnectionError);
						return;
					}
					
					// Update temporary client
					m_syncClientAdapter.changeId(m_syncClient.getSyncClientId(), syncClient.getSyncClientId());
					m_syncClientAdapter.resetCurrentClient();
					m_syncClient = syncClient;
					
					executeInBackground(new Runnable() {

						@Override
						public void run() {
							m_syncActionAdapter.buildInitialSync();
							
							generatePackage();
						}
						
					});
				}
				
			});
	}
	
	private void generatePackage() {
		notifySyncProgress(SyncState.GeneratingPackage);

		// Create change set
		List<SyncAction> syncActions = m_syncActionAdapter.getAll();
		syncActions = SyncService.foreignKeyPolicy(syncActions);
		SyncPackage syncPackage = m_syncActionAdapter.syncServer(syncActions);
		syncPackage.setSyncClientId(m_syncClient.getSyncClientId());
		syncPackage.setServerTimestamp(m_syncClient.getLastSyncServerTimestamp());

		compressPackage(syncPackage);
	}
	
	private void compressPackage(SyncPackage syncPackage) {
		Bundle notifyData = new Bundle();
		notifyData.putInt(SyncService.ProgressDataSyncActions, syncPackage.getSyncActions().size());
		notifySyncProgress(SyncState.CompressingPackage, notifyData);

		try {
		
			String jsonPackage = syncPackage.encode();
			byte[] bytePackage = jsonPackage.getBytes("UTF-8");
			byte[] gzipPackage = GzipHelper.compress(bytePackage);
			
			sendPackage(gzipPackage);
			
		} catch(Exception ex) {
			notifySyncError(SyncState.DataMalformed);
		}
	}
	
	private void sendPackage(byte[] gzipPackage) {
		Bundle notifyData = new Bundle();
		notifyData.putLong(SyncService.ProgressDataPackageSize, gzipPackage.length);
		notifySyncProgress(SyncState.SendingPackage, notifyData);
		
		WsSync.syncRequest(gzipPackage, new ISyncRequestResult() {

			@Override
			public void syncCompleted(final byte[] gzipPackage) {
				if(gzipPackage == null) {
					notifySyncError(SyncState.ServerError);
					return;
				}

				executeInBackground(new Runnable() {

					@Override
					public void run() {
						// Sync server changes
						decompressPackage(gzipPackage);
					}
					
				});
			}
			
		});
	}
	
	private void decompressPackage(byte[] gzipPackage) {
		Bundle notifyData = new Bundle();
		notifyData.putLong(SyncService.ProgressDataPackageSize, gzipPackage.length);
		notifySyncProgress(SyncState.DecompressingPackage, notifyData);
		
		try {
			
			byte[] bytePackage = GzipHelper.decompress(gzipPackage);
			String jsonPackage = new String(bytePackage, "UTF-8");
			SyncPackage syncPackage =  new SyncPackage();
			syncPackage.decode(new JSONObject(jsonPackage));
	
			applyServerActions(syncPackage);
		
		} catch(Exception ex) {
			notifySyncError(SyncState.DataMalformed);
		}
	}
	
	private void applyServerActions(SyncPackage syncPackage){
		Bundle notifyData = new Bundle();
		notifyData.putInt(SyncService.ProgressDataSyncActions, syncPackage.getSyncActions().size());
		notifySyncProgress(SyncState.ApplyingPackage, notifyData);
		
		// 1. At this point data was successfully processed by server
		// 2. Server resolves all conflicts
		// 3. User was not allowed to add any new data - no new changes
		
		boolean success = m_syncActionAdapter.syncClient(syncPackage.getSyncActions());
		if(!success) {
			notifySyncError(SyncState.ClientError);
			return;
		}
		
		complete();
	}
	
	private void complete() {
		notifySyncProgress(SyncState.Completed);
		m_isSyncing.set(false);		
		stopSelf();
	}

	
	private static List<SyncAction> foreignKeyPolicy(List<SyncAction> syncActions) {
		Collections.sort(syncActions, new Comparator<SyncAction>() {

			@Override
			public int compare(SyncAction syncActionA, SyncAction syncActionB) {

				if(syncActionA.getServerTimestamp() == syncActionB.getServerTimestamp()) {
					
					String tableA = syncActionA.getTable();
					String tableB = syncActionB.getTable();
					
					if(tableA.equals(tableB)) {
						return 0;
					}
					
					int orderA = tableA.equals("MemoBases") ? 3 : (tableA.equals("Words") ? 2 : (tableA.equals("Memos") ? 1 : 0));
					int orderB = tableB.equals("MemoBases") ? 3 : (tableB.equals("Words") ? 2 : (tableB.equals("Memos") ? 1 : 0));
					
					return orderA < orderB ? 1 : -1;
					
				} else {
					// Desc
					return syncActionA.getServerTimestamp() < syncActionB.getServerTimestamp() ? 1 : -1;
				}

			}
			
		});
		
		return syncActions;
	}
	
	private void notifySyncError(final SyncState state) {
		notifySyncError(state, null);
	}
	
	private void notifySyncError(final SyncState state, final Bundle data) {
		m_isSyncing.set(false);
		notifySyncProgress(state, data);
		stopSelf();
	}

	private void notifySyncProgress(final SyncState state) {
		notifySyncProgress(state, null);
	}
	
	private void notifySyncProgress(final SyncState state, final Bundle data) {
		if(m_syncProgress != null && m_syncProgress.get() != null) {
			m_syncProgressHandler.post(new Runnable() {

				@Override
				public void run() {
					m_syncProgress.get().onSyncProgress(state, data);
				}
				
			});
		}
	}
	
	private void executeInBackground(final Runnable runnable) {
		new WorkerThread<Void,Void,Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {
					runnable.run();
				} catch(Exception ex) {
					notifySyncError(SyncState.ProcessingError);
				}
				
				return null;
			}
			
		}.execute();
	}
}
