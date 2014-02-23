package app.memoling.android.audio;

import java.util.ArrayList;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoAdapter.Sort;
import app.memoling.android.db.DatabaseHelper.Order;
import app.memoling.android.entity.Memo;
import app.memoling.android.helper.NotificationHelper;
import app.memoling.android.ui.activity.AudioReplayActivity;

public class AudioReplayService extends Service implements ITextToSpeechUtterance {

	public final static String MemoBaseId = "MemoBaseId";
	
	private final static int AudioReplayServiceNotificationId = 4879;
	
	private TextToSpeechHelper m_textToSpeechHelper;

	private String m_lastUtterenceId;
	private ArrayList<String> m_wordsToRead;
	private int m_index;

	public static void startReplay(Context context, String memoBaseId) {
		Intent intent = new Intent(context, AudioReplayService.class);
		intent.putExtra(MemoBaseId, memoBaseId);
		context.startService(intent);
	}
	
	public AudioReplayService() {
		m_textToSpeechHelper = new TextToSpeechHelper(this);
		m_textToSpeechHelper.setSpeechRate(0.8f);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		String memoBaseId = intent.getStringExtra(MemoBaseId);
		
		if(memoBaseId != null) {
			m_index = 1;
			m_wordsToRead = new ArrayList<String>();
			
			MemoAdapter memoAdapter = new MemoAdapter(this);
			ArrayList<Memo> memos = memoAdapter.getAll(memoBaseId, Sort.CreatedDate, Order.DESC);
			
			for(Memo memo : memos) {
				if(!memo.getWordA().getWord().equals("")) {
					m_wordsToRead.add(memo.getWordA().getWord());
					m_lastUtterenceId = m_textToSpeechHelper.readText(memo.getWordA().getWord(), memo.getWordA().getLanguage(), this);
				}
				if(!memo.getWordB().getWord().equals("")) {
					m_wordsToRead.add(memo.getWordB().getWord());
					m_lastUtterenceId = m_textToSpeechHelper.readText(memo.getWordB().getWord(), memo.getWordB().getLanguage(), this);
				}
			}
		}
		
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		NotificationHelper.cancel(this, AudioReplayServiceNotificationId);
		m_textToSpeechHelper.shutdown();
		super.onDestroy();
	}

	@Override
	public void onUtteranceCompleted(String utteranceId) {

		NotificationHelper.cancel(this, AudioReplayServiceNotificationId);

		Intent audioReplayIntent = new Intent(this, AudioReplayActivity.class);

		PendingIntent pendigIntent = PendingIntent.getActivity(this, 0, audioReplayIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		Notification notification = new NotificationHelper().createNotification(this,
				R.drawable.ic_notification, "Memoling", 
				String.format("%d/%d - %s",  m_index, m_wordsToRead.size(), m_wordsToRead.get(m_index-1))
				, pendigIntent);
		m_index++;
		
		NotificationHelper.show(this, AudioReplayServiceNotificationId, notification);
		
		if(m_lastUtterenceId.equals(utteranceId)) {
			readingFinished();
		}
	}
	
	private void readingFinished() {
		NotificationHelper.cancel(this, AudioReplayServiceNotificationId);
		this.stopSelf();
	}
	
}
