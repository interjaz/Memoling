package app.memoling.android.wordoftheday;

import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import app.memoling.android.R;
import app.memoling.android.adapter.MemoAdapter;
import app.memoling.android.adapter.MemoBaseAdapter;
import app.memoling.android.adapter.WordOfTheDayAdapter;
import app.memoling.android.entity.Language;
import app.memoling.android.entity.Memo;
import app.memoling.android.entity.WordOfTheDay;
import app.memoling.android.helper.AppLog;
import app.memoling.android.ui.ApplicationActivity;
import app.memoling.android.ui.activity.WordOfTheDayReceiverActivity;
import app.memoling.android.ui.fragment.MemoListFragment;
import app.memoling.android.wordoftheday.provider.Provider;
import app.memoling.android.wordoftheday.provider.ProviderAdapter;
import app.memoling.android.wordoftheday.resolver.MemoOfTheDay;
import app.memoling.android.wordoftheday.resolver.ResolverBase;
import app.memoling.android.wordoftheday.resolver.ResolverFactory;

public class DispatcherService extends Service {

	// Lets starts somewhere safe from AlarmReceiver
	private static int m_notificationId = 1000;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		WordOfTheDayAdapter wordAdapter = new WordOfTheDayAdapter(this);
		MemoBaseAdapter memoBaseAdapter = new MemoBaseAdapter(this);
		ArrayList<WordOfTheDay> words = wordAdapter.getAll();

		Random rand = new Random();
		
		// Check if still MemoBase exists
		ArrayList<WordOfTheDay> wordOfTheDayToDelete =  new ArrayList<WordOfTheDay>();
		for(WordOfTheDay word : words) {
			if(memoBaseAdapter.get(word.getMemoBaseId()) == null) {
				wordOfTheDayToDelete.add(word);
			}
		}
		
		// Remove old ones
		for(WordOfTheDay word : wordOfTheDayToDelete) {
			wordAdapter.delete(word.getWordOfTheDayId());
			words.remove(word);
		}

		for (WordOfTheDay word : words) {
			dispatch(this, word.getMode(), rand, word.getMemoBaseId(), word.getProviderId(), word.getPreLanguageFrom(),
					word.getLanguageTo());
		}

		return START_NOT_STICKY;
	}

	public static void dispatch(Context context, WordOfTheDayMode mode, Random random, String memoBaseId,
			int providerId, Language from, Language to) {

		switch (mode) {
		case OnlineOnly:
			getOnlineMemoOfTheDay(context, memoBaseId, providerId, from, to);
			break;
		case Mixed:
			if (random.nextBoolean()) {
				getOnlineMemoOfTheDay(context, memoBaseId, providerId, from, to);
			} else {
				getMemoOfTheDay(context, memoBaseId);
			}
			break;
		case LibraryOnly:
			getMemoOfTheDay(context, memoBaseId);
			break;
		}

	}

	private static void getOnlineMemoOfTheDay(final Context context, final String memoBaseId, int providerId,
			Language from, Language to) {

		Provider provider = new ProviderAdapter().getById(providerId);
		provider.setPreTranslateToLanguage(from);
		provider.setTranslateToLanguage(to);

		ResolverBase resolver = ResolverFactory.getProvider(provider, context);

		resolver.fetch(new IFetchComplete() {
			@Override
			public void onFetchComplete(MemoOfTheDay memo) {
				if(memo == null) {
					return;
				}
				
				memo.setMemoBaseId(memoBaseId);
				createNotification(context, memo, true);
			}
		});

	}

	private static void getMemoOfTheDay(Context context, String memoBaseId) {
		MemoAdapter adapter = new MemoAdapter(context);
		Memo memo = adapter.getRandom(memoBaseId);
		createNotification(context, memo, false);
	}

	private static void createNotification(Context context, Memo memo, boolean online) {

		if (memo == null) {
			AppLog.e("DispatcherService", "createNotification failed to obtain memo");
			return;
		}

		try {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
					.setSmallIcon(R.drawable.ic_notification).setContentTitle("Memoling")
					.setContentText(memo.getWordA().getWord() + " - " + memo.getWordB().getWord());

			if (online) {
				Intent wordIntent = new Intent(context, WordOfTheDayReceiverActivity.class);
				wordIntent.putExtra(WordOfTheDayReceiverActivity.MemoOfTheDayObject, memo.serialize().toString());
				wordIntent.putExtra(WordOfTheDayReceiverActivity.NotificationId, m_notificationId);

				PendingIntent pendigIntent = PendingIntent.getActivity(context, m_notificationId, wordIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(pendigIntent);
			} else {
				Intent appIntent = new Intent(context, ApplicationActivity.class);
				appIntent.putExtra(MemoListFragment.MemoBaseId, memo.getMemoBaseId());
				appIntent.putExtra(MemoListFragment.NotificationId, m_notificationId);
				PendingIntent pendigIntent = PendingIntent.getActivity(context, m_notificationId, appIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(pendigIntent);
			}

			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(m_notificationId, builder.build());
			m_notificationId++;
		} catch (JSONException ex) {
			AppLog.e("DispatcherService", "createNotification", ex);
		}
	}

}
